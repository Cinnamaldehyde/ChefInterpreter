package interpreter;

import java.io.File;
import java.util.HashMap;
import java.util.Scanner;

public class Chef {

	public static void main(String[] args) throws Exception {
		if (args.length < 1)
			throw new Exception("No recipe found! Usage: Chef recipe.chef");
		//System.out.println("Heyyyo");
		Chef interpreter = new Chef(args[0]);
		interpreter.bake();
	}
	
	File file;
	Scanner scan;
	HashMap<String, Recipe> recipes;
	Recipe mainrecipe;
	
	public Chef(String filename) throws Exception {
		recipes = new HashMap<String, Recipe>();
		//System.out.print("Reading recipe(s) from '"+filename+"'.. ");
		file = new File(filename);
		scan = new Scanner(file);
		//scan.useDelimiter("\n\n");
		int progress = 0;
		Recipe r = null;
		String stringy = "heyyo";
		int count = 0;
		while (scan.hasNext()) { 
			count+=1;
			//System.out.println("blip" + progress);
			//-----------------------
			String line = "";
			boolean booly = true;
			while (booly){
				String tempLine = "";
				if (scan.hasNext())
					tempLine = scan.nextLine();
				if (tempLine.equals("")){
					booly = false;
				}
				else{
					line = line + tempLine + "\n";
				}
			}
			//String line = scan.next();
			//System.out.println(line);
			if (line.startsWith("Ingredients")) {
				stringy = stringy + "1";
				if (progress > 3)
					throw new ChefException(ChefException.STRUCTURAL, "Read unexpected "+progressToExpected(2)+". Expecting "+progressToExpected(progress));
				progress = 3;
				r.setIngredients(line);
			}
			else if (line.startsWith("Cooking time")) {
				stringy = stringy + "2";
				if (progress > 4)
					throw new ChefException(ChefException.STRUCTURAL, "Read unexpected "+progressToExpected(3)+". Expecting "+progressToExpected(progress));
				progress = 4;
				r.setCookingTime(line);
			}
			else if (line.startsWith("Pre-heat oven")) {
				stringy = stringy + "3";
				if (progress > 5)
					throw new ChefException(ChefException.STRUCTURAL, "Read unexpected "+progressToExpected(4)+". Expecting "+progressToExpected(progress));
				progress = 5;
				r.setOvenTemp(line);
			}
			else if (line.startsWith("Method")) {
				stringy = stringy + "4";
				if (progress > 5)
					throw new ChefException(ChefException.STRUCTURAL, "Read unexpected "+progressToExpected(5)+". Expecting "+progressToExpected(progress));
				progress = 6;
				r.setMethod(line);
			}
			else if (line.startsWith("Serves")) {
				stringy = stringy + "5";
				if (progress != 6)
					throw new ChefException(ChefException.STRUCTURAL, "Read unexpected "+progressToExpected(6)+". Expecting "+progressToExpected(progress));
				progress = 0;
				r.setServes(line);
			}
			else {
				stringy = stringy + "6";
				if (progress == 0 || progress >= 6) {
					r = new Recipe(line);
					if (mainrecipe == null)
						mainrecipe = r;
					progress = 1;
					recipes.put(parseTitle(line), r);
				}
				else if (progress == 1) {
					progress = 2;
					r.setComments(line);
				} 
				else {
					throw new ChefException(ChefException.STRUCTURAL, "Read unexpected comments/title. Expecting "+progressToExpected(progress)+" Hint:"+structHint(progress));
				}
			}
		}
		//System.out.println(count + stringy);
		if (mainrecipe == null)
			throw new ChefException(ChefException.STRUCTURAL, "Recipe empty or title missing!");
		//System.out.println("Done!");
	}
	
	private String parseTitle(String title) {
		if (title.charAt(title.length() - 1) == '.') {
			title = title.substring(0,title.length()-1);
		}
		return title.toLowerCase();
	}

	private String structHint(int progress) {
		switch (progress) {
			case 2 : return "did you specify 'Ingredients.' above the ingredient list?";
			case 3 : return "did you specify 'Methods.' above the methods?";
		}
		return "no hint available";
	}

	private String progressToExpected(int progress) {
		switch (progress) {
			case 0 : return "title";
			case 1 : return "comments";
			case 2 : return "ingredient list";
			case 3 : return "cooking time";
			case 4 : return "oven temperature";
			case 5 : return "methods";
			case 6 : return "serve amount";
		}
		return null;
	}

	public void bake() throws ChefException {
		Kitchen k = new Kitchen(recipes, mainrecipe);
		k.cook();
	}

}
