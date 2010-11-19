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
		addNumbersToWordCharacters();
		addUnderscoreToWordCharacters();
	}

	private void addUnderscoreToWordCharacters() {
		st.ordinaryChar('_');
		st.wordChars('_','_');
	}

	private void addNumbersToWordCharacters() {
		st.ordinaryChars('0', '9');
		st.wordChars('0', '9');
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
				log().debug("Number " + st.nval);
				return st.nval + "";
			case StreamTokenizer.TT_WORD:
				// A word was found; the value is in sval
				log().debug("Word " + st.sval);
				return st.sval;
			case '"':
				// A double-quoted string was found; sval contains the contents
				log().debug("DoubleQuotedString " + st.sval);
				return st.sval;
			case '\'':
				// A single-quoted string was found; sval contains the contents
				log().debug("SingleQuotedString " + st.sval);
				return st.sval;
			case StreamTokenizer.TT_EOL:
				// End of line character found
				log().debug("EOL");
				return null;
			case StreamTokenizer.TT_EOF:
				// End of file has been reached
				log().debug("EOF");
				return null;
			default:
				// A regular character was found; the value is the token itself
				char ch = (char) st.ttype;
				log().debug("Char " + ch);
				return ch + "";
			}
		} catch (IOException e) {
			throw new RuntimeException("Unexpected end of input");
		}
	}

	private Logger _log = null;

	private Logger log() {
		if (_log == null) {
			_log = Logger.getLogger(this.getClass());
		}
		return _log;
	}

}
