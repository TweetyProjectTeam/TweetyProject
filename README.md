# TweetyProject
![TweetyProject Logo](./logo/tweety_small.png "TweetyProject Logo")

TweetyProject is a collection of various Java libraries that implement approaches to different areas of artificial intelligence. In particular, it provides a general interface layer for doing research and working with different knowledge representation formalisms such as classical logics, conditional logics, probabilistic logics, and argumentation. Furthermore, TweetyProject contains libraries for dealing with agents, multi-agent systems, and dialog systems for agents, as well as belief revision, preference reasoning, preference aggregation, and action languages. A series of utility libraries that deal with e.g. mathematical optimization complement the collection.

TweetyProject is in development since 2010.

**Project website**: http://tweetyproject.org/
<br> **Newsletter**: http://tweetyproject.org/newsletter/

## Installation
Step-by-step guides can be found here:
* [Installing TweetyProject from the command line](http://tweetyproject.org/doc/install-commandline.html)
* [Installing TweetyProject in Eclipse](http://tweetyproject.org/doc/install-eclipse.html)
* [Installing the snapshot version of TweetyProject](http://tweetyproject.org/doc/install-snapshot.html)

## Contributing
Want to contribute? We appreciate it! If you find a bug or would like to request a feature, please open an issue here. For bug reports, include steps to recreate the bug and screenshots, if possible. 

If you want to make your own contributions to the code, follow the usual steps:
* Fork this repository 
* Create a new branch `git checkout -b àdd-feature`
* Make your changes 
* Add your changes  `git add your-files`
* Commit your changes `git commit -m "description of your feature"`
* Push to your branch `git push origin add-feature`
* Create a pull request`

## Documentation
* **API:** The most recent version of the technical documentation for all TweetyProject libraries in form of the JavaDoc API can be found here: [TweetyProject API 1.17](http://tweetyproject.org/api/1.17/index.html)
* **Integration of third-party solvers:** The following pages give some more detailed information on how third-party products such as SAT solvers and optimization solvers can be integrated in TweetyProject: 
   * [Integration of SAT Solvers](http://tweetyproject.org/doc/sat-solvers.html)
  * [Integration of first-order logic theorem provers](http://tweetyproject.org/doc/fol-provers.html)
  * [Integration of optimization problem solvers](http://tweetyproject.org/doc/optimization-problem-solvers.html)
  * [Integration of solvers for determining minimal unsatisfiable subsets](http://tweetyproject.org/doc/mus-enumerators.html)
* **Tutorials:** The slidedecks of the following tutorials give a more detailed and practical introduction into working with TweetyProject:
  * [Implementing KR approaches with Tweety, tutorial at the the 16th International Conference on Principles of Knowledge Representation and Reasoning (KR'18) in Tempe (USA)](http://tweetyproject.org/doc/tutorials/kr2018/index.html)
  * [Formal Argumentation Approaches in TweetyProject, tutorial at the the 4th Summer School on Argumentation (SSA'20)](http://tweetyproject.org/doc/tutorials/ssa2020/index.html)
* **Papers:** The following papers give a brief overview on the functionalities of TweetyProject and should also be used as a reference when citing TweetyProject in scientific papers.
  * Matthias Thimm. "Tweety - A Comprehensive Collection of Java Libraries for Logical Aspects of Artificial Intelligence and Knowledge Representation". In Proceedings of the 14th International Conference on Principles of Knowledge Representation and Reasoning (KR'14). Vienna, July, 2014. [PDF](http://www.mthimm.de/pub/2014/Thimm_2014.pdf) [bibtex](http://www.mthimm.de/pub/2014/Thimm_2014.bib)
  * Matthias Thimm. The Tweety Library Collection for Logical Aspects of Artificial Intelligence and Knowledge Representation. In Künstliche Intelligenz, 31(1):93-97, March 2017. [PDF](http://mthimm.de/pub/2017/Thimm_2017a.pdf) [bibtex](http://mthimm.de/pub/2017/Thimm_2017a.bib)
* **[Example code and resources](http://tweetyproject.org/doc/example-code.html)**: Example code snippets that show the functionality of the library (also distributed as part of the library).
* **[Developer guide](http://tweetyproject.org/doc/dev-guide.html)**

A manual on how to use TweetyProject in your programs is currently in development.

## License
All parts of the TweetyProject available on this website are licensed under the [GNU Lesser General Public License](http://www.gnu.org/licenses/#LGPL) version 3 (beginning with TweetyProject version 1.6, the versions before are licensed under the [GNU General Public License](http://www.gnu.org/licenses/#GPL) version 3) except when this is noted otherwise. Use and modification of the libraries is encouraged but please give credit by referring to this repository. Please note that some libraries make also use of other third-party libraries such as Apache Commons. 
