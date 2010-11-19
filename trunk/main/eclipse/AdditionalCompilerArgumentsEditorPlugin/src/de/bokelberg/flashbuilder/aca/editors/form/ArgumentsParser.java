package de.bokelberg.flashbuilder.aca.editors.form;

import org.apache.log4j.Logger;

public class ArgumentsParser implements Parser {

	private ArgumentsModel model;

	public ArgumentsParser(ArgumentsModel model) {
		this.model = model;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.bokelberg.flashbuilder.aca.editor.model.Parser#parse(java.lang.String)
	 */
	public void parse(String input) {
		log().debug("parse " + input);
		model.clear();
		doParse(input);
	}

	/**
	 * A option can have different forms, some examples -optimize
	 * -keep-as3-metadata METADATA -keep-as3-metadata=METADATA
	 * -keep-as3-metadata+=METADATA -keep-as3-metadata=METADATA1,METADATA2
	 * -keep-as3-metadata METADATA1 METADATA2 METADATA3
	 * -keep-as3-metadata+=METADATA1,METADATA2 -define NS::test true
	 * -dump-config "my dumped config.xml"
	 * 
	 * @param input
	 * @return
	 */
	private void doParse(String input) {
		if (input == null || input == "") {
			return;
		}

		Scanner sc = new ArgumentsScanner();
		sc.setInput(input);

		String token = sc.nextToken();

		for (;;) {
			if (token == null) {
				return;
			}

			if (!tokEquals(token, "-")) {
				throw new RuntimeException("Expected -, got <" + token + ">");
			}
			token = sc.nextToken();
			if (token == null) {
				throw new RuntimeException("Expected option name, got <" + null
						+ ">");
			}

			Argument arg = model.addArg("-" + token);

			token = sc.nextToken();
			if (token == null) {
				return;
			}
			if (token.equals("-")) {
				continue;
			}
			if (tokEquals(token, "=")) {
				arg.assignmentOperator = "=";
				token = sc.nextToken();
			} else if (tokEquals(token, "+")) {
				token = sc.nextToken();
				if (tokEquals(token, "=")) {
					arg.assignmentOperator = "+=";
					token = sc.nextToken();
				} else {
					throw new RuntimeException("= expected, got <" + token
							+ ">");
				}
			}

			if (token == null || tokEquals(token, "-")) {
				throw new RuntimeException(
						"one or more values expected, got null");
			}

			do {
				arg.addValue(token);
				token = sc.nextToken();
				if (tokEquals(token, ",")) {
					token = sc.nextToken();
				}
			} while( token != null && !token.equals("-"));	
		}
	}

	private boolean tokEquals(String token, String expectedToken) {
		return token != null && token.equals(expectedToken);
	}

	private Logger _log = null;

	private Logger log() {
		if (_log == null) {
			_log = Logger.getLogger(this.getClass());
		}
		return _log;
	}
}
