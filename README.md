ACSL2C - Traduction of ACSL to C
==============================
The goal of ACSL2C is to translate [ACSL](https://frama-c.com/html/acsl.html) annotations into C, in a way that allows the verification of the C code by an [SMT](en.wikipedia.org/wiki/Satisfiability_modulo_theories#) solver.
This translation currently focuses on function contracts with named behaviours, and applies an operation called "folding", which replaces function calls by a precise combination of what the function needs to be executed (the "pre-condition") and what the function ensures when it terminates (the "post-condition").
The shape of the handled contracts is for now slightly restricted: "assumes" and "requires" clauses must not contain "forall" quantifiers applied to types other than integers.

Usage
===============================
The ACSL2C tool must first be compiled into a JAR executable.
To do so, you can either use the "compile.sh" bash script, or execute the command "mvn package" from inside the root directory.
This will build the desired JAR executable, that you can then execute using the command "java -jar <path_of_the_jar_file> --working-dir=<path_of_the_working_dir> --c-file=<path_to_the_c_file>".

Required Softwares
=======================================
- [Java >= 17.0.0](https://www.oracle.com/java/technologies/downloads/)
- [Syntax](https://sourcesup.renater.fr/projects/syntax)

Contributors
=====================================

* [Quentin Nivon](https://quentinnivon.github.io/)

License
=============================
[![License](https://img.shields.io/badge/license-Apache--2.0-blue.svg)](LICENSE.txt)
