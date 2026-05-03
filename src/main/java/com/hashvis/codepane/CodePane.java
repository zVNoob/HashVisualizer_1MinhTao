package com.hashvis.codepane;

import javafx.scene.layout.StackPane;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.InputStream;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

import com.hashvis.codepane.parser.*;
import com.hashvis.codepane.parser.ast.Ast;
import com.hashvis.codepane.parser.ast.Ast.EvalException;

import javafx.embed.swing.SwingNode;

/**
 * HashFunction is a custom code editor component that embeds a Swing JTextPane
 * inside a JavaFX StackPane. It provides real-time syntax highlighting,
 * bracket matching, and an autocomplete suggestion system based on a custom
 * AST.
 */
public class CodePane extends StackPane {
  // UI components
  private Font font = loadFont(18);
  private JTextPane content = new JTextPane();
  private SuggestionOverlay popupMenu = new SuggestionOverlay(font);
  private SwingNode swingNode = new SwingNode();
  // Logic components
  protected SymbolTable symbolTable = null;
  protected ParseTree parseTree = new ParseTree("", symbolTable);
  protected EvalException evalException = null;
  private BracketParser bracketParser = new BracketParser("");
  private boolean tryEval = true;
  // Highlighter
  private TextHighlighter highlighter = null;

  /**
   * Loads the custom font.
   */
  private Font loadFont(int size) {

    try (InputStream is = getClass().getResourceAsStream("/fonts/font.ttf")) {
      if (is != null) {
        Font customFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont((float) size).deriveFont(Font.BOLD);
        return customFont;
      } else {
        return new Font("Monospaced", Font.PLAIN, size);
      }
    } catch (Exception e) {
      return new Font("Monospaced", Font.PLAIN, size);
    }
  }

  protected void validateResultType(Object obj) {
  }

  /**
   * Re-parses the entire document and refreshes highlighting.
   */
  private void validate() {
    String text = content.getText();
    parseTree = new ParseTree(text, symbolTable);
    evalException = null;
    if (tryEval)
      try {
        validateResultType(parseTree.eval());
      } catch (EvalException e) {
        evalException = e;
      } catch (Exception e) {
        evalException = new EvalException(null, e.getMessage());
      }
    bracketParser = new BracketParser(text);
    highlighter.buildHighlight(parseTree, bracketParser, evalException);
  }

  // =========================================================================
  // EVENT LISTENERS & BINDINGS
  // =========================================================================
  private void setupCaretListener() {
    content.addCaretListener(e -> {
      highlighter.buildHighlight(parseTree, bracketParser, evalException);
    });
  }

  private void setupDocumentListener() {
    content.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
      public void insertUpdate(javax.swing.event.DocumentEvent e) {
        validate();
      }

      public void removeUpdate(javax.swing.event.DocumentEvent e) {
        validate();
      }

      public void changedUpdate(javax.swing.event.DocumentEvent e) {
      }
    });
  }

  private void setupNewlineFilter() {
    AbstractDocument doc = (AbstractDocument) content.getDocument();
    doc.setDocumentFilter(new DocumentFilter() {
      @Override
      public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
          throws BadLocationException {
        // Strip out newlines from typed or pasted text
        String filtered = string.replace("\n", "").replace("\r", "");
        super.insertString(fb, offset, filtered, attr);
      }

      @Override
      public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
          throws BadLocationException {
        // Strip out newlines from replaced text
        String filtered = text.replace("\n", "").replace("\r", "");
        super.replace(fb, offset, length, filtered, attrs);
      }
    });
  }

  private void setupFocusListener() {
    content.addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent e) {
        if (content.isDisplayable()) {
          content.requestFocusInWindow();
        }
        // popupMenu.setVisible(false);
      }

      @Override
      public void focusGained(FocusEvent e) {

      }
    });
  }

  /**
   * Inserts the selected suggestion from the popup into the text pane,
   * replacing the partial identifier.
   */
  private void applySuggestion() {
    String suggestion = popupMenu.applySelected(); // This hides the menu and returns the string
    if (suggestion == null)
      return;

    SwingUtilities.invokeLater(() -> {
      // 1. Get the current token
      try {
        Ast currentToken = parseTree.getContextAt(content.getCaretPosition() - 1).getLast();
        // 2. Remove the partial word
        content.getDocument().remove(currentToken.begin(), currentToken.end() - currentToken.begin());
        // 3. Insert the full suggestion
        content.getDocument().insertString(currentToken.begin(), suggestion, null);
        // 4. Move caret to the end of the inserted word
        content.setCaretPosition(currentToken.begin() + suggestion.length());
        // 5. Validate
        validate();
      } catch (Exception e) {
      }
    });
  }

  private void setupKeyBindings() {
    InputMap im = content.getInputMap(JComponent.WHEN_FOCUSED);
    ActionMap am = content.getActionMap();

    // 1. UP Arrow
    im.put(KeyStroke.getKeyStroke("UP"), "menuUp");
    am.put("menuUp", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (popupMenu.isVisible()) {
          popupMenu.moveSelection(-1);
        }
      }
    });

    // 2. DOWN Arrow
    im.put(KeyStroke.getKeyStroke("DOWN"), "menuDown");
    am.put("menuDown", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (popupMenu.isVisible()) {
          popupMenu.moveSelection(1);
        }
      }
    });

    // 3. ENTER or TAB to Apply
    im.put(KeyStroke.getKeyStroke("ENTER"), "menuEnter");
    im.put(KeyStroke.getKeyStroke("TAB"), "menuTab");

    AbstractAction applyAction = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        applySuggestion();
      }
    };
    am.put("menuEnter", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
      }
    });
    am.put("menuTab", applyAction);
  }

  // =========================================================================
  // UI STYLING & CONSTRUCTOR
  // =========================================================================
  private static final int FIXED_HEIGHT = 45;

  /**
   * Custom ScrollBar UI to provide a modern, thin appearance.
   */
  private static class ModernScrollBarUI extends BasicScrollBarUI {
    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
      g.setColor(new Color(240, 240, 240));
      g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
      Graphics2D g2 = (Graphics2D) g.create();
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2.setColor(new Color(100, 149, 237));

      int thumbHeight = 6; // Very thin thumb
      int yOffset = (thumbBounds.height - thumbHeight) / 2;

      g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y + yOffset,
          thumbBounds.width - 4, thumbHeight, 5, 5);

      g2.dispose();
    }

    // Hide the annoying arrow buttons
    private JButton createZeroButton() {
      JButton button = new JButton();
      button.setPreferredSize(new Dimension(0, 0));
      button.setMinimumSize(new Dimension(0, 0));
      button.setMaximumSize(new Dimension(0, 0));
      return button;
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
      return createZeroButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
      return createZeroButton();
    }
  }

  public Object eval() {
    return parseTree.eval();
  }

  class SafeCaret extends DefaultCaret {
    public SafeCaret() {
      super();
    }

    @Override
    public void adjustVisibility(Rectangle r) {
      // The NPE happens inside scrollRectToVisible -> flushViewDirtyRegion
      // We only allow visibility adjustment if the component is
      // actually displayed and has a valid parent.
      if (getComponent() != null && getComponent().isDisplayable()) {
        try {
          super.adjustVisibility(r);
        } catch (NullPointerException npe) {
          // Swallow the NPE specifically during the SwingNode transition phase
          // This prevents the AWT thread from dying and stealing focus.
        }
      }
    }
  }

  public CodePane(SymbolTable symbolTable) {
    super();
    this.symbolTable = symbolTable;
    // Constraints for the JavaFX container
    this.setMinHeight(FIXED_HEIGHT);
    this.setPrefHeight(FIXED_HEIGHT);
    this.setMaxHeight(FIXED_HEIGHT);
    this.setMinWidth(100);
    this.setPrefWidth(USE_COMPUTED_SIZE);
    this.setMaxWidth(USE_COMPUTED_SIZE);
    swingNode.setFocusTraversable(true);

    // Inject Swing component
    SwingUtilities.invokeLater(() -> {
      content.setMinimumSize(new Dimension(100, FIXED_HEIGHT));
      content.setFont(font);
      content.setCaret(new SafeCaret());
      // Setup no wrap TextPane
      // (https://tips4java.wordpress.com/2009/01/25/no-wrap-text-pane/)
      JPanel noWrapPanel = new JPanel(new BorderLayout());
      noWrapPanel.add(content);
      JScrollPane scrollPane = new JScrollPane(noWrapPanel);
      // Setup a ScrollPane for the Swing component
      scrollPane.setHorizontalScrollBarPolicy(ScrollPaneLayout.HORIZONTAL_SCROLLBAR_ALWAYS);
      scrollPane.setVerticalScrollBarPolicy(ScrollPaneLayout.VERTICAL_SCROLLBAR_NEVER);
      scrollPane.setBorder(null);
      scrollPane.getHorizontalScrollBar().setUI(new ModernScrollBarUI());
      swingNode.setContent(scrollPane);
      // Setup listeners
      highlighter = new TextHighlighter(content, scrollPane, popupMenu);
      setupDocumentListener();
      setupCaretListener();
      setupFocusListener();
      setupKeyBindings();
      setupNewlineFilter();
      // JavaFX -> Swing
      swingNode.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
        if (isNowFocused) {
          // CRITICAL: We use a double-hop (Platform -> Swing)
          // to ensure the SwingNode is fully realized in the FX scene graph
          // before we ask the JTextPane to request focus.
          javafx.application.Platform.runLater(() -> {
            SwingUtilities.invokeLater(() -> {
              if (content.isDisplayable()) {
                content.requestFocusInWindow();
              }
            });
          });
        }
      });

      // Swing -> JavaFX
      content.addFocusListener(new java.awt.event.FocusAdapter() {
        @Override
        public void focusGained(java.awt.event.FocusEvent e) {
          javafx.application.Platform.runLater(() -> {
            if (!swingNode.isFocused()) {
              swingNode.requestFocus();
            }
          });
        }
      });
    });
    swingNode.setOnMousePressed(event -> {
      swingNode.requestFocus();
    });
    this.getChildren().add(swingNode);
  }

  public CodePane(SymbolTable symbolTable, String code, boolean readOnly) {
    this(symbolTable);
    SwingUtilities.invokeLater(() -> {
      tryEval = !readOnly;
      content.setEditable(!readOnly);
      content.setText(code);
      content.setCaretPosition(0);
    });
  }
}
