package worldView;



import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class Parser {
	
	
	
	ArrayList<String> tokens = new ArrayList<String>();
	
	List<String> numbers    = Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
	List<String> characters = Arrays.asList("x", "y", "(", ")", "^", "*", "/", "+", "-", 		//Basic Operations
											"sin", "cos", "tan", "asin", "acos", "atan",		//Trigonometric
											"e", "pi", "phi");									//Constants		
	
	public Parser(String expr) {

		boolean numberParsing = false;
		
		
		
		for (int i = 0; i < expr.length(); i++) {
			
			//Number parsing
			if( numbers.contains(expr.substring(i, i + 1)) ) {
				
				if (numberParsing) {
					tokens.set(tokens.size()-1, tokens.get(tokens.size()-1) + expr.substring(i, i + 1));
				}
				else {
					tokens.add(expr.substring(i, i+1));
					numberParsing = true;
				}
			}
			
			//Single character operators
			else if ( characters.contains(expr.substring(i, i+1)) ) {
				
				tokens.add(expr.substring(i, i+1));
				numberParsing = false;
			}
			
			//Double character operators 			   (checking if 2 more characters exist)
			else if ( expr.substring(i).length()>2 ) { if ( characters.contains(expr.substring(i, i+2)) ) {
				
				tokens.add(expr.substring(i, i+2));
				i += 1;
				numberParsing = false;
			}}
			
			
			//Triple character operators 			   (checking if 3 more characters exist)
			else if ( expr.substring(i).length()>2 ) { if ( characters.contains(expr.substring(i, i+3)) ) {
				
				tokens.add(expr.substring(i, i+3));
				i += 2;
				numberParsing = false;
			}}
			
			
			//Quadruple character operators 		   (checking if 4 more characters exist)
			else if ( expr.substring(i).length()>3 ) { if (characters.contains(expr.substring(i, i+4))) {
				
				tokens.add(expr.substring(i, i+4));
				i += 3;
				numberParsing = false;
			}}
		}
	}
	
	
	
	public double evaluate(double x, double y) {
		
		int expressionStart;
		int expressionEnd;
		
		List<String> eval = new ArrayList<String>(tokens.size());
		for (String i : tokens)
			eval.add(i);
		
		while (eval.contains("(")) {
			expressionStart = eval.lastIndexOf("(");
			expressionEnd = eval.subList(expressionStart, eval.size()).indexOf(")") + expressionStart;
			eval.set( expressionStart, mutate(eval.subList(expressionStart + 1, expressionEnd), x, y) );
			
			for (int i = 0; i < 2; i++)
				eval.remove(expressionStart + 1);
		}
		
		return Double.parseDouble(mutate(eval, x, y));
	}
	
	
	
	private String mutate(List<String> expression, double x, double y) {
		
		int place;
		
		
		//Inject x and y
		while (expression.contains("x"))
			expression.set(expression.indexOf("x"), ""+x);
		while (expression.contains("y"))
			expression.set(expression.indexOf("y"), ""+y);
		
		
		
		//Fundamental constants need to be parseable
		while (expression.contains("e"))
			expression.set( expression.indexOf("e"), ""+Math.E );
		while (expression.contains("pi"))
			expression.set( expression.indexOf("pi"), ""+Math.PI );
		while (expression.contains("phi"))
			expression.set( expression.indexOf("phi"), ""+1.61803398874989 );
		
		
		
		//Trigonometric values
		while (expression.contains("sin")) {
			place = expression.indexOf("sin");
			expression.set( place, ""+Math.sin(Double.parseDouble(expression.get(place + 1))) );
			expression.remove(place + 1);
		}
		while (expression.contains("cos")) {
			place = expression.indexOf("cos");
			expression.set( place, ""+Math.cos(Double.parseDouble(expression.get(place + 1))) );
			expression.remove(place + 1);
		}
		while (expression.contains("tan")) {
			place = expression.indexOf("tan");
			expression.set( place, ""+Math.tan(Double.parseDouble(expression.get(place + 1))) );
			expression.remove(place + 1);
		}
		while (expression.contains("asin")) {
			place = expression.indexOf("asin");
			expression.set( place, ""+Math.asin(Double.parseDouble(expression.get(place + 1))) );
			expression.remove(place + 1);
		}
		while (expression.contains("acos")) {
			place = expression.indexOf("acos");
			expression.set( place, ""+Math.acos(Double.parseDouble(expression.get(place + 1))) );
			expression.remove(place + 1);
		}
		while (expression.contains("atan")) {
			place = expression.indexOf("atan");
			expression.set( place, ""+Math.atan(Double.parseDouble(expression.get(place + 1))) );
			expression.remove(place + 1);
		}
		
		//Order of Operations
		while (expression.contains("^")) {
			place = expression.indexOf("^");
			expression.set( place - 1, ""+Math.pow(Double.parseDouble(expression.get(place - 1)), Double.parseDouble(expression.get(place + 1))) );
			expression.remove(place);
			expression.remove(place);
		}
		while (expression.contains("*")) {
			place = expression.indexOf("*");
			expression.set( place - 1, ""+( Double.parseDouble(expression.get(place - 1)) * Double.parseDouble(expression.get(place + 1))) );
			expression.remove(place);
			expression.remove(place);
		}
		while (expression.contains("/")) {
			place = expression.indexOf("/");
			expression.set( place - 1, ""+( Double.parseDouble(expression.get(place - 1)) / Double.parseDouble(expression.get(place + 1))) );
			expression.remove(place);
			expression.remove(place);
		}
		while (expression.contains("+")) {
			place = expression.indexOf("+");
			expression.set( place - 1, ""+( Double.parseDouble(expression.get(place - 1)) + Double.parseDouble(expression.get(place + 1))) );
			expression.remove(place);
			expression.remove(place);
		}
		while (expression.contains("-")) {
			place = expression.indexOf("-");
			expression.set( place - 1, ""+( Double.parseDouble(expression.get(place - 1)) - Double.parseDouble(expression.get(place + 1))) );
			expression.remove(place);
			expression.remove(place);
		}
		
		return expression.get(0);
	}
}
