package com.hashvis.codepane;

import javax.swing.*;

import com.hashvis.codepane.parser.*;
import com.hashvis.codepane.parser.ast.*;
import com.hashvis.codepane.parser.ast.Ast.EvalException;
import com.hashvis.codepane.parser.func.BuiltinFunction;

import java.awt.*;
import java.awt.event.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * SuggestionOverlay provides a floating autocomplete menu for the code editor.
 * It displays a "breadcrumb" path of the current AST context and a list of
 * suggested symbols (identifiers) retrieved from the SymbolTable.
 */
public class SuggestionOverlay extends JPopupMenu {
  private List<JMenuItem> items = new ArrayList<>();
  private List<String> texts = new ArrayList<>();
  private int selectedIndex = -1;
  private final Color highlightColor = new Color(100, 149, 237, 100); // Semi-transparent blue
  private Font font;

  /**
   * Initializes the overlay with a specific font to match the editor's
   * typography.
   * 
   * @param font The font to be used for the suggestion text.
   */
  public SuggestionOverlay(Font font) {
    super();
    this.font = font;
    // We keep focusable false so the JTextPane keeps the keyboard
    setFocusable(false);
  }

  /** Overridden to prevent the popup from consuming mouse events. */
  @Override
  protected void processMouseEvent(MouseEvent e) {
  }

  @Override
  protected void processMouseMotionEvent(MouseEvent e) {
  }

  /**
   * Clears all current suggestions and hides the menu.
   */
  public void reset() {
    setVisible(false);
    removeAll();
    items.clear();
    texts.clear();
    selectedIndex = -1;
  }

  /**
   * Generates a navigation string (breadcrumb) based on the AST context.
   * Example: "myArray > [0] > member"
   * 
   * @param context A list of AST nodes from the root down to the cursor.
   * @return A formatted string representing the path to the current cursor.
   */
  private String buildBreadcumb(java.util.List<Ast> context) {
    String breadcumb = "";
    for (Ast ast : context) {
      String temp = ast.getNavic();
      if (temp.length() > 0)
        breadcumb += " > " + temp;
    }
    if (breadcumb.length() > 0)
      breadcumb = breadcumb.substring(3);
    return breadcumb;
  }

  /**
   * Creates a detailed menu item containing the symbol name and its metadata
   * (type/docs).
   * 
   * @param text  The name of the symbol (e.g., "sin", "myVar").
   * @param value The actual object associated with the symbol in the SymbolTable.
   */
  private void buildMenuItem(String text, Object value) {
    // Prepare text and docs
    String docs = "";
    if (value == null)
      docs = "unknown";
    if (value instanceof BuiltinFunction) {
      text += "("; // Indicate that it's a function
      docs = ((BuiltinFunction) value).docs();
    }
    if (value instanceof BigInteger)
      docs = "Integer";

    if (value instanceof ArrayList)
      docs = "Array";

    JMenuItem item = new JMenuItem() {
      @Override
      protected void processMouseEvent(MouseEvent e) {
      }

      protected void processMouseMotionEvent(MouseEvent e) {
      }
    };
    item.setOpaque(true);
    item.setLayout(new BorderLayout());
    // Left side: The symbol name
    JLabel lblKey = new JLabel(text);
    lblKey.setFont(font);
    item.add(lblKey, BorderLayout.WEST);
    // Right side: The documentation/type (italicized and smaller)
    JLabel lblValue = new JLabel(docs);
    lblValue.setFont(lblKey.getFont().deriveFont(Font.ITALIC).deriveFont((float) 15));
    item.add(lblValue, BorderLayout.EAST);
    // Calculate preferred width to fit both labels
    int width = lblKey.getPreferredSize().width + lblValue.getPreferredSize().width + 15;
    item.setPreferredSize(new Dimension(width, 20));
    addItem(item);
    texts.add(text);
  }

  /**
   * Updates the suggestions based on the current AST context and SymbolTable.
   * 
   * @param context The current AST hierarchy at the cursor position.
   * @param symTab  The symbol table used to query available identifiers.
   */
  public void update(java.util.List<Ast> context, SymbolTable symTab, EvalException e) {
    reset();
    // Report any errors
    if (e != null) {
      JMenuItem errorItem = new JMenuItem() {
        @Override
        protected void processMouseEvent(MouseEvent e) {
        }

        protected void processMouseMotionEvent(MouseEvent e) {
        }
      };
      errorItem.setLayout(new BorderLayout());
      errorItem.setEnabled(false);
      JLabel errorLbl = new JLabel(e.getMessage());
      errorLbl.setForeground(Color.RED);
      errorItem.add(errorLbl, BorderLayout.WEST);
      errorItem.setPreferredSize(new Dimension(errorLbl.getPreferredSize().width + 15, 20));
      add(errorItem);
    }
    // Add breadcumb
    JMenuItem breadcumbItem = new JMenuItem(buildBreadcumb(context));
    breadcumbItem.setEnabled(false);
    breadcumbItem.setForeground(Color.GRAY);
    add(breadcumbItem);
    addSeparator();
    // Add suggestions
    if (context.size() > 0)
      if (context.getLast() instanceof Id) {
        Id id = (Id) context.getLast();
        for (java.util.Map.Entry<String, Object> item : symTab.query(id.content()).entrySet()) {
          buildMenuItem(item.getKey(), item.getValue());
        }
      }
  }

  private void addItem(JMenuItem item) {
    items.add(item);
    add(item);
  }

  public int getCount() {
    return items.size();
  }

  /**
   * Changes the currently selected suggestion.
   * 
   * @param direction Positive (1) to move down, negative (-1) to move up.
   */
  public void moveSelection(int direction) {
    if (items.isEmpty())
      return;

    // 1. Clear previous highlight
    if (selectedIndex >= 0 && selectedIndex < items.size()) {
      items.get(selectedIndex).setBackground(null);
      // items.get(selectedIndex).setOpaque(false);
    }

    // 2. Update index (with wrapping)
    selectedIndex = (selectedIndex + direction + items.size()) % items.size();

    // 3. Apply new highlight
    JMenuItem selectedItem = items.get(selectedIndex);
    selectedItem.setBackground(highlightColor);
    selectedItem.scrollRectToVisible(selectedItem.getBounds());

    repaint();
  }

  /**
   * Returns the text of the selected suggestion and hides the overlay.
   * 
   * @return The string of the selected item, or null if nothing is selected.
   */
  public String applySelected() {
    if (selectedIndex >= 0 && selectedIndex < items.size()) {
      // Close the menu after selection
      setVisible(false);
      return texts.get(selectedIndex);
    }
    return null;
  }
}
