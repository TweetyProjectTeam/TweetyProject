call mvn deploy:deploy-file -Dfile="lib/net-sf-tweety-cli/jspf.core-1.0.2.jar" -DgroupId="net.sf.tweety.dependencies" -DartifactId="jspf" -Dversion="1.0.2" -Dpackaging="jar" -DrepositoryId="nexus-angerona" -Durl="http://ls1-www.cs.tu-dortmund.de/nexus/content/repositories/angerona-releases"
echo Exit Code = %ERRORLEVEL%
if not "%ERRORLEVEL%" == "0" exit /b

call mvn deploy:deploy-file -Dfile="lib/net-sf-tweety-math/ojalgo-35.0.jar" -DgroupId="net.sf.tweety.dependencies" -DartifactId="ojalgo" -Dversion="35" -Dpackaging="jar" -DrepositoryId="nexus-angerona" -Durl="http://ls1-www.cs.tu-dortmund.de/nexus/content/repositories/angerona-releases"
echo Exit Code = %ERRORLEVEL%
if not "%ERRORLEVEL%" == "0" exit /b