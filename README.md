ACSL2C - Traduction of ACSL to C
==============================
The goal of ACSL2C is to translate [ACSL](https://frama-c.com/html/acsl.html) annotations into C, in a way that allows the verification of the C code by an [SMT](en.wikipedia.org/wiki/Satisfiability_modulo_theories#) solver.
This translation currently focuses on function contracts with named behaviours, and applies an operation called "folding", which replaces function calls by a precise combination of what the function needs to be executed (the "pre-condition") and what the function ensures when it terminates (the "post-condition").
The shape of the handled contracts is for now slightly restricted: "assumes" and "requires" clauses must not contain "forall" quantifiers applied to types other than integers.

Usage
===============================


Required Softwares
=======================================

Important information
======================================
The VBPMN tool relies on the CADP toolbox that is updated once per month.
If you upgrade CADP on your machine, VBPMN must also be upgraded to a newer version, compliant with the most recent
version of CADP.
This can be done by [**downloading again the VBPMN WAR file
**](https://quentinnivon.github.io/vbpmn/latest/transformation.war)
and replacing the old WAR file of the Tomcat by the new one.
If you have any issue with a subsequent version of VBPMN, please send an email to quentin.nivon@inria.fr.

Browser Compatibility
====================================
The web app has been tested on the following browsers.

* Mozilla Firefox 47.0
* Google Chrome 51.0.2704

Contributors
=====================================

* [Pascal Poizat](http://pascalpoizat.github.io/)
* [Gwen Salaün](http://convecs.inria.fr/people/Gwen.Salaun/)
* [Ajay Krishna](https://about.me/ajaykrishna)
* [Quentin Nivon](https://quentinnivon.github.io/)

License
=============================
[![License](https://img.shields.io/badge/license-Apache--2.0-blue.svg)](LICENSE.md)

Web pages
============
The website is built on top of:

- [Bootstrap](http://getbootstrap.com/), version 3.0.3, licenced under Apache Licence 2.0
- [Font Awesome](http://fortawesome.github.io/Font-Awesome/), version 4.1.0, licenced under SIL OFL 1.1 (desktop and
  webfont files) and MIT Licence (css and less files)
- [Glyph Icons Halflings](http://glyphicons.com/), released under the same licence as Bootstrap