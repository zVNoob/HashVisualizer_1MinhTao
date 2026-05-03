package com.hashvis.codepane;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.hashvis.codepane.BracketParser.BracketPosition;
import com.hashvis.codepane.parser.*;
import com.hashvis.codepane.parser.ast.*;
import com.hashvis.codepane.parser.ast.Ast.EvalException;
import com.hashvis.codepane.parser.ast.nonterm.*;
import com.hashvis.codepane.parser.ast.nonterm.haspreexec.HasPreExec;
import com.hashvis.codepane.parser.func.BuiltinFunction;

/**
 * The TextHighlighter class is responsible for applying syntax highlighting
 * to the code editor component.
 */
public class TextHighlighter {
  // Syntax highlighting styles
  private SimpleAttributeSet builtinColor = new SimpleAttributeSet();
  private SimpleAttributeSet numberColor = new SimpleAttributeSet();
  private SimpleAttributeSet lambdaColor = new SimpleAttributeSet();
  private SimpleAttributeSet opColor = new SimpleAttributeSet();
  private SimpleAttributeSet errorColor = new SimpleAttributeSet();
  private SimpleAttributeSet exceptionColor = new SimpleAttributeSet();
  private SimpleAttributeSet bracketColor = new SimpleAttributeSet();
  // UI references
  private JTextPane content;
  private SuggestionOverlay popupMenu;
  private JScrollPane scrollPane;

  /**
   * Constructor for the TextHighlighter class.
   */
  public TextHighlighter(JTextPane content, JScrollPane scrollPane, SuggestionOverlay popupMenu) {
    this.content = content;
    this.popupMenu = popupMenu;
    this.scrollPane = scrollPane;
    StyleConstants.setForeground(builtinColor, new Color(0, 102, 255));
    StyleConstants.setForeground(numberColor, new Color(201, 103, 52));
    StyleConstants.setForeground(lambdaColor, new Color(0, 186, 0));
    StyleConstants.setForeground(opColor, new Color(150, 0, 200));
    StyleConstants.setForeground(errorColor, new Color(255, 85, 85));
    StyleConstants.setBackground(bracketColor, new Color(255, 184, 108));
    StyleConstants.setBackground(exceptionColor, new Color(255, 194, 194));
  }

  // =========================================================================
  // SYNTAX HIGHLIGHTING LOGIC
  // =========================================================================
  private void applyTextColor(Ast ast, SimpleAttributeSet style) {
    int start = ast.begin();
    int length = ast.end() - ast.begin();

    if (length > 0)
      applyTextColor(start, length, style);
  }

  private void applyTextColor(int start, int length, SimpleAttributeSet style) {
    try {
      content.getStyledDocument().setCharacterAttributes(start, length, style, false);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Recursively traverses the AST to apply colors based on the node type.
   */
  private void buildHighlightRecursive(Ast ast) {
    if (ast == null)
      return;
    // Traverse the AST to apply colors recursively
    if (ast instanceof HasPreExec) {
      HasPreExec hasPreExec = (HasPreExec) ast;
      buildHighlightRecursive(hasPreExec.preExec());
    }
    if (ast instanceof NonTerm) {
      NonTerm nonTerm = (NonTerm) ast;
      for (Ast child : nonTerm.children())
        buildHighlightRecursive(child);
    }
    // Apply colors
    if (ast instanceof Id) {
      Id id = (Id) ast;
      Object temp = id.scope().get(id.content());
      if (temp instanceof BuiltinFunction) {
        applyTextColor(id, builtinColor);
      }
      return;
    }
    if (ast instanceof Int) {
      Int id = (Int) ast;
      applyTextColor(id, numberColor);
      return;
    }
    if (ast instanceof Lambda) { // Highlight "{",":" and "}"
      applyTextColor(ast.begin(), 1, lambdaColor);
      for (int i = 0; i < ast.content().length(); i++) {
        if (ast.content().charAt(i) == ':') {
          applyTextColor(ast.begin() + i, 1, lambdaColor);
        }
      }
      if (ast.content().charAt(ast.content().length() - 1) == '}') {
        applyTextColor(ast.end() - 1, 1, lambdaColor);
      }
      return;
    }
    // Likely a parse error
    if (ast.getClass() == Ast.class || ast.getClass() == HasPreExec.class)
      applyTextColor(ast, errorColor);
  }

  static final String opsChars = "+*/-%^&|<>=!";

  /** Simple pass to highlight operator characters. */
  private void buildHighlightOp() {
    String text = content.getText();
    for (int i = 0; i < text.length(); i++) {
      if (opsChars.indexOf(text.charAt(i)) != -1) {
        applyTextColor(i, 1, opColor);
      }
    }
  }

  private void resetStyles() {
    StyledDocument doc = content.getStyledDocument();
    SimpleAttributeSet defaultStyle = new SimpleAttributeSet();
    StyleConstants.setForeground(defaultStyle, Color.BLACK);
    StyleConstants.setBackground(defaultStyle, Color.WHITE);
    doc.setCharacterAttributes(0, doc.getLength(), defaultStyle, false);
  }

  /**
   * Highlights matching brackets based on the cursor position.
   * Mismatched brackets are colored in 'errorColor'.
   */
  private void buildHighlightPair(BracketParser bracketParser) {
    for (BracketPosition bp : bracketParser.data().values()) {
      if (bp.counterpart == null)
        applyTextColor(bp.position, 1, errorColor);
    }
    int caretPos = content.getCaretPosition();
    ArrayList<BracketParser.BracketPosition> pair = bracketParser.get(caretPos - 1);
    if (pair.size() == 0)
      pair = bracketParser.get(caretPos);
    if (pair.size() == 0)
      return;
    if (pair.size() == 1) {
      applyTextColor(pair.get(0).position, 1, bracketColor);
      applyTextColor(pair.get(0).position, 1, errorColor);
    }
    if (pair.size() == 2) {
      applyTextColor(pair.get(0).position, 1, bracketColor);
      applyTextColor(pair.get(1).position, 1, bracketColor);
    }
  }

  private void buildHighlightException(EvalException evalException) {
    if (evalException == null) {
      content.setToolTipText(null);
      return;
    }
    if (evalException.ast() == null) {
      applyTextColor(0, content.getText().length(), exceptionColor);
      content.setToolTipText(evalException.getMessage());
      return;
    }
    int start = evalException.ast().begin();
    int end = evalException.ast().end();
    applyTextColor(start, end - start, exceptionColor);
    content.setToolTipText(evalException.getMessage());
  }

  private void buildPopup(ParseTree parseTree, EvalException evalException) {
    popupMenu.reset();
    java.util.List<Ast> context = parseTree.getContextAt(content.getCaretPosition() - 1);
    SymbolTable currentSymTab = parseTree.symbolTable();
    for (Ast ast : context)
      if (ast instanceof Lambda)
        currentSymTab = ((Lambda) ast).scope();
    popupMenu.update(context, currentSymTab, evalException);
    SwingUtilities.invokeLater(() -> {
      popupMenu.show(scrollPane, 0, scrollPane.getHeight());
      content.requestFocusInWindow();
    });
  }

  private boolean inBuildHighlight;

  /** Refreshes highlighting. */
  public void buildHighlight(ParseTree tree, BracketParser bracketParser, EvalException evalException) {
    if (inBuildHighlight)
      return;
    inBuildHighlight = true;
    SwingUtilities.invokeLater(() -> {
      if (!inBuildHighlight) {
        return;
      }
      resetStyles();
      buildHighlightRecursive(tree.tree());
      buildHighlightOp();
      buildHighlightPair(bracketParser);
      buildHighlightException(evalException);
      if (content.isEditable())
        buildPopup(tree, evalException);
      inBuildHighlight = false;
    });
  }

}
