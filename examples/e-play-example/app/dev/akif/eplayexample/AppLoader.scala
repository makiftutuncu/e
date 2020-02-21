package dev.akif.eplayexample

import play.api.{Application, ApplicationLoader, LoggerConfigurator}

class AppLoader extends ApplicationLoader {
  override def load(context: ApplicationLoader.Context): Application = {
    LoggerConfigurator(context.environment.classLoader).foreach { loggerConfigurator =>
      loggerConfigurator.configure(context.environment)
    }

    new AppComponents(context).application
  }
}
