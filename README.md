# Java User Inputs Validator

[![Latest Version on Packagist](https://img.shields.io/packagist/v/girover/tree.svg?style=flat-square)](https://packagist.org/packages/girover/tree)
[![GitHub Tests Action Status](https://img.shields.io/github/workflow/status/girover/tree/run-tests?label=tests)](https://github.com/girover/tree/actions?query=workflow%3Arun-tests+branch%3Amain)
[![GitHub Code Style Action Status](https://img.shields.io/github/workflow/status/girover/tree/Check%20&%20fix%20styling?label=code%20style)](https://github.com/girover/tree/actions?query=workflow%3A"Check+%26+fix+styling"+branch%3Amain)


---
## Content

  - [Introduction](#introduction)
  - [prerequisites](#prerequisites)
  - [Usage](#usage)
    - [Validator Class](#validator-class)
    - [Writing rules](#writing-rules)
    - [All available rules](#all-available-rules)
    - [Displaying error messages](#displaying-error-messages)
    - [Customizing error messages](#customizing-error-messages)
    - [Validation exception](#validation-exception)
  - [Class Diagram](#class-diagram)
  - [Changelog](#changelog)
  - [Contributing](#contributing)
  - [Security Vulnerabilities](#security-vulnerabilities)
  - [Credits](#credits)
  - [License](#license)


## Introduction
**girover/javaFxValidation** is a package that allows you to validate all javaFX inputs.   
With this package it will be very simple to validate inputs without writing much code.   


## prerequisites

- Java
- Ms SQL Server
- JavaFx

# Usage
---
## Validator Class
To start validating user inputs, you must create an instance of **javaFxValidation.Validator** class.   
The constructor of the `Validator` takes an object as a parameter, which is the class that you want to validate their fields against some rules.   
Supposing you have a controller containing some `javaFx` components like `TextField` and you want to validate its value, then you must pass this controller to the constructor of `Validator`, afterthat you should call the method `validate()` *OR* `validate(String...fields)`.   

```java
import javafx.scene.control.TextField;
import javaFxValidation.Validator;

public class MainController {

    @FXML
    private TextField name;

	public void onClickBtnSave(ActionEvent event) {
		try{
			// Create instance of javaFXValidation.Validator		
			Validator validator = new Validator(this);
			// Start validation
			validator.validate();
			
		}catch(ValidationException e){
			System.out.println(e.getMessage);
		}
		
	}
}
```
The above code will do nothing but creating instance of `Validator`, because we have not assigned any rules to the field that we want to validate.   

To tell `validator` to start validating fields in the controller, there are two things you must to do.   
***First*** you must add `@Rules` Annotation to the field you want to validate.   
This annotation takes two values. The first is `field` which is the field name, and the second is `rules` which is a String containing all rules you want validate this field against. [Go to writing rules section](#writing-rules).   
***Second*** you must call `validator.validate()` or `validator.validate(String...fieldNames)`.   
The difference between these two methods is that `validate()` will validate all fields annotated with `@Rules` in the controller, While `validate(String...fields)` will validate only the fields you pass to this method as Strings *(those fields must be annotated by @rule annotation)*   

*Look at this example:*

```java
import javafx.scene.control.TextField;
import javaFxValidation.Validator;
import javaFxValidation.annotations.Rules;

public class MainController {

    @FXML
    @Rules(field="user name", rules="required")
    private TextField name;

	public void onClickBtnSave(ActionEvent event) {
		try{
			Validator validator = new Validator(this);
		}catch(ValidationException e){
			System.out.println(e.getMessage);
		}
		
	}
}
```

> **Note** 
> The field name in the annotation does not have to be the same as the variable name.

In this example we added annotation `Rules` before the field name, and we give the field a name `field= "user name"` and we pass wanted rules `rules="required"`. [All available rules](#all-available-rules).


After the validator has validated wanted fields, you can check if fields pass the rules or not by calling the method `pass()` or `pass(boolean)`.   


```java
	if(validator.pass())
		System.out.println("all fields are valid");
	else
		System.out.println("Failed to pass all rules");
```

The method `pass` will also generate all **error messages** for all rules that the field does not pass.   
But when a field fails to pass a specific rule, why to check all rules that come after this rule.   
In this case you can pass `true` as an argument to the method `pass(boolean)` to tell the validator to stop checking rules when the first failure occurs.   

**Example**

```java
	boolean stopOnFirstFailure = true;
	if(validator.pass(stopOnFirstFailure))
		System.out.println("all fields are valid");
	else
		System.out.println("Failed to pass all rules");
```
.
.
.

## Writing rules

This package uses two types of rules `explicit` and `parameterized`.   
The difference between these two types is that **parameterized** rules accept parameters,   
while `explicit` rules do not.   

As mentioned in the section [Validator Class](#validator-class), **rules** are passed by `@Rules` annotation   
`@Rules(field = "email", rules = ""required")` as a **String**.   
But this string has a special form. Rules are separated from each other by using ` | ` character.   
For instance:

```ruby 

	@FXML
    @Rules(field = "email", rules = "required|email")
    TextField email;

```

As you can see the field **email** has two **rules** to pass. These rules are `required` and `email`.   
**required** means that the value of this field can not be null or empty. **email** means that the value of this field must be a valid email address.   
There is no limitation for how many rules you write in one string.
***But what if a rule takes parameters. How to pass parameters to the rule?***
It is very simple to pass parameters to the rule by using ` : ` character between rule name and parameters.
For example: `@Rules(field = "name", rules = "required|min:2|max:20")`.   
This means name must be provided because of **required**, the length of the name can not be shorter than 2 letters   
and the length of name can not be longer than 20.   
There are also some rules accept more than one parameter, so how to pass these parameters?   
It is also very simple by using ` , ` character between parameters.   
For example: `@Rules(field = "user role", rules = "required|in:admin,student,teacher")`.   
This means that **user role** must be **admin**, **student** or **teacher**.

> **Note**
> All fields are optional by default, which means no rules will be applied to them, if their values are null or empty string. But when using `required` rule, so the value can not be null or empty and all other rules also will be applied. Look at this example.

```ruby
import javaFxValidation.Validator;

public class MainController {

    @Rules(field="user name", rules="length:10")
    private String name = "";

	public void onClickBtnSave(ActionEvent event) {
		
		try {
			Validator validator = new Validator(this);
			validator.validate();
			
			if(validator.pass())
				System.out.println("Passed successfully");
				
		} catch (ValidationException e) {
			e.printStackTrace();
		}
	}
}
```
The value of field `name` is an empty string and the rule `required` is not given to it, so the rule `length:10` will not be applied to this field. But if the value of `name` is not empty, then the rule `length:10` will be applied and ***error message*** will be generated if this value is not exactly 10 characters.

## All available rules

There are two types of rules `explicit` and `parameterized`   
`explicit` rules do not take any parameter, 
.
.
.


**Parameterized Rules**
| #   | Rule Name    		   | Description                                           |
| --- | -------------------------- | ----------------------------------------------------- |
|1    | **`digits`**| The `field` must be `parameter` digits. |
|2    | **`between`** | The `field` must be between `param-1` and `param-2`. |
|3    | **`in`**  | The `field` must be in `parameters`. |
|4    | **`notIn`**| The `fiend` cannot be in `parameters`.  |
|6	  |	**`max`**    |  The `field` must not be greater than `param`.  |
|7	  |	**`min`**  | The `field` must be at least `param`. |
|8	  |	**`digits_max`**     | The `field` must not have more than `param` digits.  |
|9	  |	**`digits_min`**     | The `field` must have at least `param` digits. |
|10   |	**`length`**   | The length of `field` must be `param`. |
|11	  | **`length_max`**   | The length of `field` must not be longer than `param`. |
|12	  | **`length_min`**  | The length of `field` must not be shorter than `param`. |
|13	  | **`gt`**    | The `field` must be greater than `param`. |
|14	  |	**`gte`**   | The `field` must be greater than or equal to `param`. |
|15	  | **`lt`**    | The `field` must be less than `param`. |
|16	  | **`lte`**  | The `field` must be less than or equal to `param`. |
|17	  | **`mime`** | The `field` accepts only extensions: `parameters`. |
|18	  | **`format`** | The format of `field` must be `param`. |
|19	  | **`regex`** | The `field` not matches the Regular Expression `param`. |
|20	  | **`same`** | The `field-1` doesn't match `field-2`. |

**All Rules**
| #   | Exlpicit Rules (Do not take Parameters)	   | Parameterized Rules (Accepts parameters)   |
| --- | -------------------------- | ----------------------------------------------------- |
|1    |[**`required`**](#required) | **`digits`**|
|2    | **`alphaNumeric`** | **`between`**  |
|3    | **`alphaDash`** |**`in`**  | 
|4    | **`numeric`** |**`notIn`**  |
|6	  | **`email`** |**`max`**    |
|7	  | **`date`** |**`min`**    |
|8	  | **`boolean`**  |**`digits_max`**     |
|9	  ||**`digits_min`**    |
|10   ||**`length`**   |
|11	  ||**`length_max`** |
|12	  ||**`length_min`**|
|13	  ||**`gt`**    |
|14	  ||**`gte`**   |
|15	  ||**`lt`**    |
|16	  ||**`lte`**  |
|17	  ||**`mime`** |
|18	  ||**`format`** |
|19	  ||**`regex`** |
|20	  ||**`same`**  |


#### Required
Required means that the field cann't be null or blank String.  [:arrow_up:](#all-available-rules)

```java
	String name = "";
		
	validator.addFieldRules("name", name, "required");
```

#### alphaNumeric
The field under validation must only contain letters and numbers.  [:arrow_up:](#all-available-rules)
> Note
> This cann't accept spaces

```java
	String group = "group999";
		
	validator.addFieldRules("group name", group, "alphaNumeric");
```

#### alphaDash
The field under validation must only contain letters, numbers, dashes and underscores.  [:arrow_up:](#all-available-rules)
> Note
> This cann't accept spaces

```java
	String CPR = "123456-1233";
		
	validator.addFieldRules("CPR", CPR, "alphaDash");
```

#### numeric
The field under validation must be a number.  [:arrow_up:](#all-available-rules)

```java
	String age = "40";
		
	validator.addFieldRules("Age", age, "numeric");
```

#### email
The field under validation must be a valid email address.  [:arrow_up:](#all-available-rules)

```java
	String email = "example@domain.com";
		
	validator.addFieldRules("Email", email, "email");
```

#### date
The field under validation must be a valid date.  [:arrow_up:](#all-available-rules)

```java
	String birthDate = "01/01/1990";
		
	validator.addFieldRules("birthDate", birthDate, "date");
```

#### boolean
The field must be true or false.  [:arrow_up:](#all-available-rules)

```java
	String booleanField = "1";
		
	validator.addFieldRules("booleanField", booleanField, "boolean");
```
### Parameterized Rules

#### digits:*value*
The integer under validation must have an exact length of value.  [:arrow_up:](#all-available-rules)

```java
	String CPR = "1";
		
	validator.addFieldRules("CPR", CPR, "digits:10");
```
#### between:*min,max*
The integer under validation must have an exact length of value. The field under validation must have a size between the given min and max. Strings and numerics are evaluated in the same fashion as the size rule.  [:arrow_up:](#all-available-rules)

```java
	String age = "25";
	String password = "secret";
		
	// age value must be between 18 and 50.
	validator.addFieldRules("Age", age, "between:18,50");
	// the length of password must be between 5 and 15 characters.
	validator.addFieldRules("Password", password, "between:5,15");
```
#### in:*foo,bar,...*
The field under validation must be included in the given list of values.  [:arrow_up:](#all-available-rules)

```java
	String userRole = "admin";
		
	validator.addFieldRules("user role", userRole, "in:admin,editor,anotherRole");
```


## Displaying error messages

To get all ***error messages*** after calling the method `pass()`, you can call the method `getErrorMessages`.   
This method will get all error messages generated by `pass()` method as `ArrayList<String>`.   

Let us take a look at an example.

```ruby
import javaFxValidation.Validator;

public class MainController {
	@rules(field = "name", rules = "length:10")
	private String name      = "yourName";
	
	@rules(field = "birth date", rules = "required|format:dd-mm-yyyy")
	private String birthDate = "01/01/1990";
	
	@rules(field = "CPR", rules = "required|digits:10")
	private String CPR       = "123456789";
	
	@rules(field = "email address", rules = "required|email")
	private String email     = "example@domain@com";

	public void onClickBtnSave(ActionEvent event) {
		
		
		try {
			Validator validator = new Validator(this);
			validator.validate();
			
			if(validator.pass())
				System.out.println("Passed successfully");
			else
				for(String errorMessage : validator.getErrorMessages())
					System.out.println(errorMessage);
		} catch (ValidationException e) {
			e.printStackTrace();
		}
	}
}
```
The result of this code will be:  
```
The length of name must be 10.   
The date format of birth date must be dd-mm-yyyy.   
The CPR Field must be 10 digits.***   
The email address must be a valid email address.   
```

## Customizing error messages

The messages we got from the example in the previous section are default messages.   
But you can override messages for any rule on any field.   
To do that you must add the annotation `@Msg` before the field, and give it the rule name and the message. 
Assuming you want to change the error message for `email` rule.

```java 

	@Rules(field = "email address", rules = "required|email")
	@Msg(rule = "required", message = "We need your email address. Please provide it.")
	@Msg(rule = "email", message = "Please type a valid E-mail address.")
	private TextField emailAddress;

	public void onClickBtnSave(ActionEvent event) {
		try {
			Validator validator = new Validator(this);
			validator.validate();
			
			if(validator.pass())
				System.out.println("Passed successfully");
			else
				for(String errorMessage : validator.getErrorMessages())
					System.out.println(errorMessage);
					
		} catch (ValidationException e) {
			e.printStackTrace();
		
		}
	}
		
```

The result of this code will be:  

```
We need your email address. Please provide it. 
Please type a valid E-mail address.
```


## Class Diagram

![Screenshot_20230226_113949](https://user-images.githubusercontent.com/53403538/221405583-d9e4a9a4-065f-49b2-bec2-67010c1b83b7.png)

## Validation exception
.
.
.
.
## Changelog

Please see [CHANGELOG](CHANGELOG.md) for more information on what has changed recently.

## Contributing

Please see [CONTRIBUTING](.github/CONTRIBUTING.md) for details.

## Security Vulnerabilities

Please review [our security policy](../../security/policy) on how to report security vulnerabilities.

## Credits

- [Majed Girover](https://github.com/girover)
- [All Contributors](../../contributors)

## License

The MIT License (MIT). Please see [License File](LICENSE.md) for more information.
