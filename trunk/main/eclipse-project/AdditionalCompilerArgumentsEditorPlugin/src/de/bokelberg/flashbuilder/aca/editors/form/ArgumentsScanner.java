package de.bokelberg.flashbuilder.aca.editors.form;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;

import org.apache.log4j.Logger;

public class ArgumentsScanner implements Scanner {

	private StreamTokenizer st = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.bokelberg.flashbuilder.aca.editor.model.Scanner#setInput(java.lang
	 * .String)
	 */
	public void setInput(String input) {
		st = new StreamTokenizer(new StringReader(input));
		st.ordinaryChars('0', '9');
		st.ordinaryChar('_');
		st.wordChars('0', '9');
		st.wordChars('_','_');
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.bokelberg.flashbuilder.aca.editor.model.Scanner#nextToken()
	 */
	public String nextToken() {
		try {
			int tokenType = st.nextToken();

			switch (tokenType) {
			case StreamTokenizer.TT_NUMBER:
				// A number was found; the value is in nval
				debug("Number " + st.nval);
				return st.nval + "";
			case StreamTokenizer.TT_WORD:
				// A word was found; the value is in sval
				debug("Word " + st.sval);
				return st.sval;
			case '"':
				// A double-quoted string was found; sval contains the contents
				debug("DoubleQuotedString " + st.sval);
				return st.sval;
			case '\'':
				// A single-quoted string was found; sval contains the contents
				debug("SingleQuotedString " + st.sval);
				return st.sval;
			case StreamTokenizer.TT_EOL:
				// End of line character found
				debug("EOL");
				return null;
			case StreamTokenizer.TT_EOF:
				// End of file has been reached
				debug("EOF");
				return null;
			default:
				// A regular character was found; the value is the token itself
				char ch = (char) st.ttype;
				debug("Char " + ch);
				return ch + "";
			}
		} catch (IOException e) {
			throw new RuntimeException("Unexpected end of input");
		}
	}

	private Logger log = null;

	private void debug(String msg) {
		if (log == null) {
			log = Logger.getLogger(this.getClass());
		}
		log.debug(msg);
	}

}
