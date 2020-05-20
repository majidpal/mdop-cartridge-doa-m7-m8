# What is Cartridge?

A Cartridge is a set of resources that are loaded into the Platform for a particular project. They may contain anything from a simple reference implementation for a technology to a set of best practice examples for building, deploying, and managing a technology stack that can be used by a project.

This cartridge consists of a miscellaneous set of Jenkins jobs to be used in the lab sessions for modules 7 and 8 in the DevOps Academy course.
## Source code repositories

The cartridge loads the source code repositories -

* [spring-petclinic](https://github.com/spring-projects/spring-petclinic.git)

## Jenkins Jobs

This cartridge generates the following jenkins jobs to -

- Module 7:
	- Build and deploy a sample Spring Petclinic war file to a Tomcat host (which itself is deployed using a CloudFormation template in AWS)
- Module 8:
	- Spike the CPU usage of a specified host for 5 minutes so that we can trigger an AWS CloudWatch alarm

# License
Please view [license information](LICENSE.md) for the software contained on this image.

## Documentation
Documentation will be captured within this README.md and this repository's Wiki.

## Issues
If you have any problems with or questions about this image, please feel free to contact us.

## Contribute
You are invited to contribute new features, fixes, or updates, large or small; we are always thrilled to receive pull requests, and do our best to process them as fast as we can.

Before you start to code, we recommend discussing your plans, especially for more ambitious contributions. This gives other contributors a chance to point you in the right direction, give you feedback on your design, and help you find out if someone else is working on the same thing.
