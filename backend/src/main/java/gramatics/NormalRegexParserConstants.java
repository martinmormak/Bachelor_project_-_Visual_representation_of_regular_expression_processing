/* Generated By:JavaCC: Do not edit this line. NormalRegexParserConstants.java */
package gramatics;


/** 
 * Token literal values and constants.
 * Generated by org.javacc.parser.OtherFilesGen#start()
 */
public interface NormalRegexParserConstants {

  /** End of File. */
  int EOF = 0;
  /** RegularExpression Id. */
  int CHAR = 1;
  /** RegularExpression Id. */
  int STAR = 2;
  /** RegularExpression Id. */
  int PLUS = 3;
  /** RegularExpression Id. */
  int QUESTIONER = 4;
  /** RegularExpression Id. */
  int ALTERNATIVE = 5;
  /** RegularExpression Id. */
  int LEFTCLOSER = 6;
  /** RegularExpression Id. */
  int RIGHTCLOSER = 7;
  /** RegularExpression Id. */
  int UNKNOWN_TOKEN = 8;
  /** RegularExpression Id. */
  int EOL = 9;

  /** Lexical state. */
  int DEFAULT = 0;

  /** Literal token values. */
  String[] tokenImage = {
    "<EOF>",
    "<CHAR>",
    "\"*\"",
    "\"+\"",
    "\"?\"",
    "\"|\"",
    "\"(\"",
    "\")\"",
    "\"UNKNOWN_TOKEN\"",
    "\"\\n\"",
  };

}
