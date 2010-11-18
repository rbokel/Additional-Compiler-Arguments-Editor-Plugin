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
		debug("parse " + input);
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

		String token = null;

		for (;;) {
			// token equals - if we read it before somewhere below
			if (token == null || !token.equals("-")) {
				token = sc.nextToken();
			}
			if (token == null) {
				return;
			}
			if (!token.equals("-")) {
				throw new RuntimeException("Expected -, got <" + token + ">");
			}
			token = sc.nextToken();

			Argument arg = model.addArg("-" + token);

			token = sc.nextToken();
			if (token == null || token.equals("-")) {
				continue;
			} else if (token.equals("=")) {
				arg.assignmentOperator = "=";
				token = sc.nextToken();
			} else if (token.equals("+")) {
				token = sc.nextToken();
				if (!token.equals("=")) {
					throw new RuntimeException("= expected, got <" + token
							+ ">");
				}
				arg.assignmentOperator = "+=";
				token = sc.nextToken();
			}

			while (token != null && !token.equals("-")) {
				arg.addValue(token);
				token = sc.nextToken();
				if (token == null || token.equals(",")) {
					token = sc.nextToken();
				}
			}
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
