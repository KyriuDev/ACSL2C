package constants;

/**
 * Name:        CProgram.java
 * Content:	    This class provides some examples of (syntactically correct) C programs.
 * 				They are used for test purposes and will rather probably be replaced by C programs provided on
 * 				command line.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    25/02/26
 */

public class CProgram
{
	private CProgram()
	{

	}

	public static final String PROGRAM_1 =
		"int main() {" +
			"int x = 5;" +
			"int y = 7;" +
			"int sum = x + y;" +

			"return 0;" +
		"}"
	;

	public static final String PROGRAM_1_WITH_LINE_BREAKS =
		"int main() {\n" +
			"int x = 5;\n" +
			"int y = 7;\n" +
			"int sum = x + y;\n\n" +

			"return 0;\n" +
		"}"
	;

	public static final String PROGRAM_1_WITH_COMMENT =
		"//This is a comment\n" +
		"int main() {\n" +
			"int x = 5;\n" +
			"int y = 7;\n" +
			"int sum = x + y;\n\n" +

			"return 0;\n" +
		"}"
	;

	public static final String PROGRAM_1_WITH_MULTIPLE_LINES_COMMENTS =
		"/* This is a \n" +
		"* multiple lines\n" +
		"* comment */\n" +
		"int main() {\n" +
			"int x = 5;\n" +
			"int y = 7;\n" +
			"int sum = x + y;\n\n" +

			"return 0;\n" +
		"}"
	;

	public static final String PROGRAM_2_WITH_LINE_BREAKS =
		"int sum(int x, int y){\n" +
			"return x + y;\n" +
		"}\n" +
		"\n" +
		"int main() {\n" +
			"int x = 5;\n" +
			"int y = 7;\n" +
			"int sum = sum(x,y);\n\n" +

			"return sum;\n" +
		"}"
	;

	public static final String PROGRAM_SPIN_PAPER =
		"extern int __VERIFIER_nondet_int(void);\n" +
		"\n" +
		"//@ requires y>=0;\n" +
		"//@ ensures \\result = \\old(x) + \\old(y);\n" +
		"int sum(int x, int y){\n" +
			"while (y>0) {\n" +
				"x++; y--;\n" +
			"}\n" +
			"return x;\n" +
		"}\n" +
		"\n" +
		"//@ requires x>=0;\n" +
		"//@ ensures \\result = \\old(y) - \\old(x);\n" +
		"int diff(int x, int y){\n" +
			"while (x>0) {\n" +
				"x--; y--;\n" +
			"}\n" +
			"return y;\n" +
		"}\n" +
		"\n" +
		"int main(){\n" +
			"int x = __VERIFIER_nondet_int();\n" +
			"int y = __VERIFIER_nondet_int();\n" +
			"//@ assume(x >= 0 && y>= 0);\n" +
			"\n" +
			"int a = sum(x, y);\n" +
			"int b = diff(x, y);\n" +
			"int z = 2 * y;\n" +
			"\n" +
			"//@ assert((a-b) == z);\n" +
			"\n" +
			"return 0;\n" +
		"}"
	;

	public static final String PROGRAM_TEST_PARSING_WRITING =
		"/*\n" +
		"    External function used for non-deterministic\n" +
		"    ints generation.\n" +
		"*/\n" +
		"extern int __VERIFIER_nondet_int(void);\n" +
		"\n" +
		"//@ requires y>=0;\n" +
		"//@ ensures \\result = \\old(x) + \\old(y);\n" +
		"int sum(int x, int y){\n" +
			"while (true) {\n" +
				"x++; y--; //test\n" +
			"}\n" +
			"return x;\n" +
		"}\n" +
		"\n" +
		"//@ requires x>=0;\n" +
		"//@ ensures \\result = \\old(y) - \\old(x);\n" +
		"int diff(int x, int y){\n" +
			"while (x>0) {\n" +
				"/*\n" +
				"dégueulasse ceci dit\n" +
				"*/\n" +
			"}\n" +
			"return y + x;\n" +
		"}\n" +
		"\n" +
		"int main(){\n" +
			"int x = __VERIFIER_nondet_int();\n" +
			"int y = __VERIFIER_nondet_int();\n" +
			"//@ assume(x >= 0 && y>= 0);\n" +
			"\n" +
			"2 * x;\n" +
			"sum(x, y);\n" +
			"int a = sum(x, y);\n" +
			"int b = diff(x, y);\n" +
			"int z = 2 * y;\n" +
			"int w = -7894;\n" +
			"_Bool bool = true;\n" +
			"\n" +
			"//@ assert((a-b) == z);\n" +
			"\n" +
			"return;\n" +
		"}"
	;
}
