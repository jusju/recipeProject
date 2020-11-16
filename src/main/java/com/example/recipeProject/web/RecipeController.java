package com.example.recipeProject.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.recipeProject.domain.Category;
import com.example.recipeProject.domain.CategoryRepository;
import com.example.recipeProject.domain.CookingStep;
import com.example.recipeProject.domain.CookingStepRepository;
import com.example.recipeProject.domain.Ingredient;
import com.example.recipeProject.domain.IngredientRepository;
import com.example.recipeProject.domain.LoginRepository;
import com.example.recipeProject.domain.MeasuringUnitRepository;
import com.example.recipeProject.domain.Recipe;
import com.example.recipeProject.domain.RecipeRepository;
import com.example.recipeProject.domain.ForgotFormRepository;

import com.example.recipeProject.domain.SignupForm;
import com.example.recipeProject.domain.ForgotForm;

import com.example.recipeProject.domain.Login;



@Controller
public class RecipeController {

	@Autowired
	private ForgotFormRepository forgotRepository;
	@Autowired
	private RecipeRepository repository;
	@Autowired
	private CategoryRepository crepository;
	@Autowired
	private MeasuringUnitRepository murepository;
	@Autowired
	private IngredientRepository irepository;
	@Autowired
	private CookingStepRepository csrepository;
	@Autowired
    private LoginRepository lrepository; 
	
    @RequestMapping(value = "signup")
    public String addStudent(Model model){
    	System.out.println("SAVE2SAVE2");
    	model.addAttribute("signupform", new SignupForm());
        return "signup";
    }	

    @RequestMapping(value = "forgot")
    public String forgotPassword(Model model){
    	System.out.println("FORGOTPASSWORD");
    	model.addAttribute("forgotForm", new ForgotForm());
        return "forgotpassword";
    }	

    @RequestMapping(value = "sendforgot", method = RequestMethod.POST)
    public String sendforgotPassword(@ModelAttribute("forgotForm") ForgotForm forgotForm, Model model) {
    	System.out.println("FORGOTPASSWORD SEND");
    	Random random = new Random();
    	int randomNumber = random.nextInt(20000000);
    	forgotForm.setSecret(randomNumber + "");
    	System.out.println(forgotForm.getEmail());
    	forgotRepository.save(forgotForm);
    	Sahkoposti email = new Sahkoposti();
    	email.lahetaSahkoposti("jtjuslin","Armtv1990!3l33th4x", "jukka.juslin@haaga-helia.fi", "Reset key", "Secret number: " + randomNumber );
    	model.addAttribute("signupform", new SignupForm());
        return "signup";
    }	
    
    
    @RequestMapping(value = "saveuser", method = RequestMethod.POST)
    public String save(@Valid @ModelAttribute("signupform") SignupForm signupForm, BindingResult bindingResult) {
    	System.out.println("SAVESAVE");
    	if (!bindingResult.hasErrors()) { // validation errors
    		System.out.println(bindingResult);
    		System.out.println("ERRORS");
    		if (signupForm.getPassword().equals(signupForm.getPasswordCheck())) { // check password match		
        		System.out.println("NOTERRORS");			
    			String pwd = signupForm.getPassword();
		    	BCryptPasswordEncoder bc = new BCryptPasswordEncoder();
		    	String hashPwd = bc.encode(pwd);
		    	Login newUser = new Login();	
		    	ForgotForm forgot = forgotRepository.findByEmail(signupForm.getEmail());
		    	Login login = lrepository.findByEmail(signupForm.getEmail());
		    	
		    	if((forgot.getSecret()).equals(signupForm.getSecret())) {
		    		newUser.setId(login.getId());
		    	}
		    	

		    	newUser.setPasswordHash(hashPwd);
		    	newUser.setUsername(signupForm.getUsername());
		    	newUser.setRole("USER");
		    	newUser.setEmail(signupForm.getEmail());
		    	System.out.println("JUKKA " + newUser);
		    	if (lrepository.findByUsername(signupForm.getUsername()) == null) { // Check if user exists
		    		lrepository.save(newUser);
					System.out.println("fetch all USERS");

		    	}
		    	else {
	    			bindingResult.rejectValue("username", "err.username", "Username already exists");    	
	    			return "signup";		    		
		    	}
    		}
    		else {
    			bindingResult.rejectValue("passwordCheck", "err.passCheck", "Passwords does not match");    	
    			return "signup";
    		}
    	}
    	else {
    		return "signup";
    	}
    	return "redirect:/login";    	
    }   
    
    
	
	@RequestMapping(value={"/recipelist", "/"}, method=RequestMethod.GET)
	public String recipeList(Model model) {
		model.addAttribute("category", new Category());
		model.addAttribute("recipes", repository.findAll());
		model.addAttribute("categories", crepository.findAll());
		return "recipelist";
	}
	
	
	@RequestMapping(value={"/recipelist", "/"}, method=RequestMethod.POST)
	public String recipeByCatList(Category category, Model model) {
		List<Recipe> recipes = repository.findByCategory(crepository.findById(category.getCategoryId()).get());
		
		model.addAttribute("recipes", recipes);
		model.addAttribute("category", new Category());
		model.addAttribute("categories", crepository.findAll());
		return "recipelist";
	}
	
	
	@RequestMapping(value="/recipe/{id}", method=RequestMethod.GET)
	public String showRecipe(@PathVariable("id") long id, Model model) {
		Recipe x = repository.findById(id).get();
		Collections.sort(x.getSteps(), CookingStep.csStep);
		model.addAttribute("recipe", x);
		return "recipe";
	}
	
	//REST Show all Recipes.
	@RequestMapping(value="/recipes", method=RequestMethod.GET)
	public @ResponseBody List<Recipe> recipeListRest() {
		return (List<Recipe>) repository.findAll();
	}
	
	//REST Show all Ingredients.
	@RequestMapping(value="/ingredients", method=RequestMethod.GET)
	public @ResponseBody List<Ingredient> ingredientListRest() {
		return (List<Ingredient>) irepository.findAll();
	}
	
	//REST Show all CookingSteps.
	@RequestMapping(value="/cookingsteps", method=RequestMethod.GET)
	public @ResponseBody List<CookingStep> cookingStepListRest() {
		return (List<CookingStep>) csrepository.findAll();
	}
		
	//REST Show Recipe by ID.
	@RequestMapping(value="/recipes/{id}", method=RequestMethod.GET)
	public @ResponseBody Optional<Recipe> findRecipesRest(@PathVariable("id") long recipeId) {
		return repository.findById(recipeId);
	}
	
	@RequestMapping(value="/add", method=RequestMethod.GET)
	public String addRecipe(Model model) {
		Recipe one = new Recipe();
		//Creating 10 spaces for ingredients and CookingSteps.
		for (int i = 0; i < 5; i++) {
			one.addIngredient(new Ingredient());
		}
		
		for (int i = 0; i < 5; i++) {
			one.addStep(new CookingStep());
		}
		model.addAttribute("recipe", one);
		model.addAttribute("categories", crepository.findAll());
		model.addAttribute("measuringunits", murepository.findAll());
		return "addrecipe";
	}
	
	@RequestMapping(value="/save", method=RequestMethod.POST)
	public String saveRecipe(Recipe recipe) {
		
		Recipe x;
		ArrayList<Ingredient> ing = new ArrayList<>();
		ArrayList<CookingStep> cs = new ArrayList<>();
		
		//Saving new recipes.
		if (recipe.getRecipeId() == 0) {
			x = new Recipe();
			x.setCategory(recipe.getCategory());
			x.setDescription(recipe.getDescription());
			x.setName(recipe.getName());
			x.setServings(recipe.getServings());
			repository.save(x);
			
			for (Ingredient ingredient : recipe.getIngredients()) {
				if (ingredient.getName().equals("") == false) {
					ingredient.setRecipe(x);
					irepository.save(ingredient);
				} 
			}
			
			int i = 1;
			for (CookingStep step : recipe.getSteps()) {
				if (step.getRecipe() != null) {
					step.setStepNum(i);
					csrepository.save(step);
					i++;
				} else if (step.getDescription().equals("") == false) {
					step.setRecipe(x);
					step.setStepNum(i);
					csrepository.save(step);
					i++;
				} 
			}
		//Saving edited recipes.
		} else {
			x = recipe;
			
			for (Ingredient ingredient : x.getIngredients()) {
				if (ingredient.getName().equals("") == true) {
					ingredient = null;
				} else {
					ingredient.setRecipe(x);
					ing.add(ingredient);
				}
			}
			
			int i = 1;
			for (CookingStep step : x.getSteps()) {
				if (step.getDescription().equals("") == true) {
					step = null;
				} else {
					step.setStepNum(i);
					step.setRecipe(x);
					cs.add(step);
					i++;
				}
			}
			
			x.setIngredients(ing);
			x.setSteps(cs);
			x.setCategory(recipe.getCategory());
			x.setDescription(recipe.getDescription());
			x.setName(recipe.getName());
			x.setServings(recipe.getServings());
			repository.save(x);
			
		}

		return "redirect:recipelist";
	}
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@RequestMapping(value="/delete/{id}", method=RequestMethod.GET) 
	public String deleteRecipe(@PathVariable("id") long id, Model model) {
		
		Recipe x = repository.findById(id).get();
		
		for (CookingStep step : x.getSteps()) {
			csrepository.delete(step);
		}
		
		for (Ingredient ingredient : x.getIngredients()) {
			irepository.delete(ingredient);
		}
		
		repository.deleteById(id);
		return "redirect:../recipelist";
	}
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@RequestMapping(value="/edit/{id}", method=RequestMethod.GET)
	public String editRecipe(@PathVariable("id") long id, Model model) {
		Recipe one = repository.findById(id).get();
		Collections.sort(one.getSteps(), CookingStep.csStep);
		
		model.addAttribute("recipe", one);
		model.addAttribute("categories", crepository.findAll());
		model.addAttribute("measuringunits", murepository.findAll());
		return "editrecipe";
	}
	
	@RequestMapping(value="/login", method=RequestMethod.GET)
	public String login() {
		return "login";
	}
	
	//ADD PAGE Remove and Add Ingredients and CookingSteps.
	//Adding an ingredient to the ADD PAGE ingredient-list.
	@RequestMapping(value="/add", method=RequestMethod.POST, params="action=AddIngredient") 
	public String addIngredient(Recipe recipe, Model model) {
		recipe.addIngredient(new Ingredient());;
		
		model.addAttribute("recipe", recipe);
		model.addAttribute("categories", crepository.findAll());
		model.addAttribute("measuringunits", murepository.findAll());
		return "addrecipe";
	}
	
	//Removing an ingredient from the ADD PAGE ingredient-list.
	@RequestMapping(value="/add", method=RequestMethod.POST, params="action=RemoveIngredient") 
	public String removeIngredient(Recipe recipe, Model model) {
		recipe.getIngredients().remove(recipe.getIngredients().size() - 1);
		
		model.addAttribute("recipe", recipe);
		model.addAttribute("categories", crepository.findAll());
		model.addAttribute("measuringunits", murepository.findAll());
		return "addrecipe";
	}
	
	//Adding a cookingstep to the ADD PAGE directions-list.
	@RequestMapping(value="/add", method=RequestMethod.POST, params="action=AddStep") 
	public String addStep(Recipe recipe, Model model) {
		recipe.addStep(new CookingStep());;
		
		model.addAttribute("recipe", recipe);
		model.addAttribute("categories", crepository.findAll());
		model.addAttribute("measuringunits", murepository.findAll());
		return "addrecipe";
	}
	//Removing a cookingstep from the ADD PAGE directions-list.
	@RequestMapping(value="/add", method=RequestMethod.POST, params="action=RemoveStep") 
	public String removeStep(Recipe recipe, Model model) {
		recipe.getSteps().remove(recipe.getSteps().size() - 1);
		
		model.addAttribute("recipe", recipe);
		model.addAttribute("categories", crepository.findAll());
		model.addAttribute("measuringunits", murepository.findAll());
		return "addrecipe";
	}
	
	//EDIT PAGE Remove and Add Ingredients and CookingSteps.
	//Adding a cookingstep to the EDIT PAGE directions-list.
	@RequestMapping(value="/edit", method=RequestMethod.POST, params="action=AddStep") 
	public String addEditStep(Recipe recipe, Model model) {
		recipe.addStep(new CookingStep());;
			
		model.addAttribute("recipe", recipe);
		model.addAttribute("categories", crepository.findAll());
		model.addAttribute("measuringunits", murepository.findAll());
		return "editrecipe";
	}
	
	//Removing a cookingstep from the EDIT PAGE directions-list.
	@RequestMapping(value="/edit", method=RequestMethod.POST, params="action=RemoveStep") 
	public String removeEditStep(Recipe recipe, Model model) {
		recipe.getSteps().remove(recipe.getSteps().size() - 1);
			
		model.addAttribute("recipe", recipe);
		model.addAttribute("categories", crepository.findAll());
		model.addAttribute("measuringunits", murepository.findAll());
		return "editrecipe";
	}
	
	//Adding an ingredient to the EDIT PAGE ingredient-list.
	@RequestMapping(value="/edit", method=RequestMethod.POST, params="action=AddIngredient") 
	public String addEditIngredient(Recipe recipe, Model model) {
		recipe.addIngredient(new Ingredient());;
			
		model.addAttribute("recipe", recipe);
		model.addAttribute("categories", crepository.findAll());
		model.addAttribute("measuringunits", murepository.findAll());
		return "editrecipe";
	}
		
	//Removing an ingredient from the EDIT PAGE ingredient-list.
	@RequestMapping(value="/edit", method=RequestMethod.POST, params="action=RemoveIngredient") 
	public String removeEditIngredient(Recipe recipe, Model model) {
		recipe.getIngredients().remove(recipe.getIngredients().size() - 1);
			
		model.addAttribute("recipe", recipe);
		model.addAttribute("categories", crepository.findAll());
		model.addAttribute("measuringunits", murepository.findAll());
		return "editrecipe";
	}
	
}
