# Jellyfish
Jellyfish is a product suite for the System Descriptor (SD) which is a Architecture-as-Code construct modeled after the
DevOps Infrastructure-as-Code paradigm.  Jellyfish is made up of three main components:

1.  The System Descriptor language is a simple text based language that describes the architecture of a system or
product.  The **jellyfish-systemdescriptor-dsl** project contains the language's grammar, parser, editor, and other
components that deal with the DSL directly.
1.  The **jellyfish-systemdescriptor** project contains the API and default implementations that interact with the 
System Descriptor language.  Developers use this project to build custom tooling that interacts with System Descriptor
projects.  Developers rarely use the DSL directly; they should use the APIs provided by this project.
1.  The **jellyfish-cli** project contains the standard command line interface tooling built on top of the System
Descriptor language.  This project uses the API and may be extended by developers to add custom features without having
to build tooling from scratch.  
