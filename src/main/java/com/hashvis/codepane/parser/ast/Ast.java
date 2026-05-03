package com.hashvis.codepane.parser.ast;

/**
 * The Ast class is the base class for all AST nodes.
 * Also serve as raw token holder
 */
public class Ast {
  protected int _begin;
  protected int _end;
  private String _content;

  public Ast(int begin, int end, String content) {
    this._begin = begin;
    this._end = end;
    this._content = content;
  }

  public int begin() {
    return this._begin;
  }

  public int end() {
    return this._end;
  }

  public String content() {
    return this._content;
  }

  /**
   * Return the token context, used for IDE features
   */
  public String getNavic() {
    return this._content;
  }

  public static class EvalException extends RuntimeException {
    private Ast _ast;

    public EvalException(Ast ast, String message) {
      super(message);
      this._ast = ast;
    }

    public Ast ast() {
      return this._ast;
    }
  }

  public Object eval() {
    throw new EvalException(this, "Invalid expression");
  }

  @Override
  public String toString() {
    return this._begin + ":" + this._end + " (Raw)" + this._content;
  }
}
