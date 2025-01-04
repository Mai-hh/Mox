## Mox

Mox is a lightweight, dynamically typed programming language whose syntax and semantics closely follow the Lox language. It is designed to be easy to learn, fun to use, and simple to hack on. If you’re familiar with Lox, you will be right at home with Mox.

### Features
	•	Simplicity: Minimal syntax for a quick learning curve
	•	Dynamically Typed: No need to declare variable types
	•	Garbage Collected: Automatic memory management
	•	Object-Oriented: Classes, inheritance, and methods built-in
	•	First-Class Functions: Functions are treated like any other value (store them in variables, pass them around, return them from other functions)
	•	Closures: Lexical scoping allows functions to capture and carry their environments
	•	Embeddable: Integrates easily into larger applications
	•	Interactive: REPL support for rapid experimentation

### Quick Example

```Lox
// Hello world in Mox
print "Hello, world!";

// Variables and arithmetic
var a = 10;
var b = 20;
print a + b; // Prints 30

// Functions
fun greet(name) {
  print "Hello, " + name + "!";
}

greet("Mox"); // Prints "Hello, Mox!"
```

### Object-Oriented Programming in Mox

Mox supports classes, inheritance, instance variables, and methods. Each class defines its own behaviors and properties. Subclasses can extend parent classes to inherit their fields and methods.

#### Class Definition

```Lox
class Person {
  init(name, age) {
    this.name = name;
    this.age = age;
  }
  
  sayHello() {
    print "Hello, my name is " + this.name + ". I am " + this.age + " years old.";
  }
}

var bob = Person("Bob", 30);
bob.sayHello();
// Output: Hello, my name is Bob. I am 30 years old.
```

#### Inheritance

Use extends to create a subclass that inherits from another class.

```Lox
class Developer < Person {
  init(name, age, language) {
    // Call the parent class's initializer
    super.init(name, age);
    this.language = language;
  }

  code() {
    print this.name + " is coding in " + this.language + ".";
  }
}

var alice = Developer("Alice", 25, "Mox");
alice.sayHello(); 
// Output: Hello, my name is Alice. I am 25 years old.

alice.code();
// Output: Alice is coding in Mox.
```

#### Methods and Dynamic Dispatch

Within a class, you can define methods that operate on the current instance using this. Subclasses can override methods to provide specialized behavior, and Mox will dynamically dispatch method calls at runtime.

```Lox
class Animal {
  speak() {
    print "Some generic animal sound.";
  }
}

class Dog extends Animal {
  speak() {
    print "Woof!";
  }
}

var dog = Dog();
dog.speak(); // Output: Woof!
```

#### Installation

Using Homebrew (macOS/Linux)

```bash
brew tap mai-hh/mox
```

```bash
brew install mox
```

Building from Source
	1.	Clone the repository:

```bash
git clone https://github.com/yourusername/mox.git
```

Usage

Running a File

To run a Mox script, simply pass the script file as an argument:

```bash
mox path/to/script.mox
```

Interactive REPL

You can also start an interactive REPL by running Mox without any arguments:

```bash
mox
```

This allows you to experiment with Mox code one line at a time.

Contributing

Contributions are welcome! Feel free to submit pull requests and report issues.
	1.	Fork the repository
	2.	Create a new branch for your feature or fix
	3.	Commit and push your changes
	4.	Open a pull request

License

Mox is open source software distributed under the MIT License. You are free to use it for any purpose, personal or commercial.

Happy Moxing! If you have any questions, feel free to open an issue or start a discussion.
