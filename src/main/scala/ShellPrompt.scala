//package com.bookish.config

//import sbt.{Project, State, ProcessLogger}

/**
  * Default shell prompt for projects that use git
  * @author Mike Slinn
  */
/*class ShellPrompt(lines: Stream[String]) {

  object devnull extends ProcessLogger {
    def info(s: => String) {}

    def error(s: => String) {}

    def buffer[T](f: => T): T = f
  }

  val current = """\*\s+([\w-]+)""".r

  def gitBranches = ("git branch --no-color" lines_! devnull mkString)

  val buildShellPrompt = {
    (state: State) => {
      val currBranch = current findFirstMatchIn gitBranches map (_ group (1)) getOrElse "-"
      val currProject = Project.extract(state).currentProject.id
      "%s:%s> ".format(currProject, currBranch)
    }
  }
}*/
