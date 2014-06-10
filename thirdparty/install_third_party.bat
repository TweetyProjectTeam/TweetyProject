call mvn install:install-file -Dfile="lib/net-sf-tweety-math/qoca.jar" -DgroupId="net.sf.tweety.dependencies" -DartifactId="qoca" -Dversion="1.0.0" -Dpackaging="jar"
echo Exit Code = %ERRORLEVEL%
if not "%ERRORLEVEL%" == "0" exit /b

call mvn install:install-file -Dfile="lib/net-sf-tweety-cli/jspf.core-1.0.2.jar" -DgroupId="net.sf.tweety.dependencies" -DartifactId="jspf" -Dversion="1.0.2" -Dpackaging="jar"
echo Exit Code = %ERRORLEVEL%
if not "%ERRORLEVEL%" == "0" exit /b

call mvn install:install-file -Dfile="lib/net-sf-tweety-math/lpsolve55j.jar" -DgroupId="net.sf.tweety.dependencies" -DartifactId="lpsolve" -Dversion="5.5" -Dpackaging="jar"
echo Exit Code = %ERRORLEVEL%
if not "%ERRORLEVEL%" == "0" exit /b

call mvn install:install-file -Dfile="lib/net-sf-tweety-math/ojalgo-35.0.jar" -DgroupId="net.sf.tweety.dependencies" -DartifactId="ojalgo" -Dversion="35" -Dpackaging="jar"
echo Exit Code = %ERRORLEVEL%
if not "%ERRORLEVEL%" == "0" exit /b