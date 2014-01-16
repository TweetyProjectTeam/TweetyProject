call mvn deploy:deploy-file -Dfile="lib/net-sf-tweety-math/choco-1_2_03.jar" -DgroupId="net.sf.tweety.dependencies" -DartifactId="choco" -Dversion="1.2.03" -Dpackaging="jar" -DrepositoryId="nexus-angerona" -Durl="http://ls1-www.cs.tu-dortmund.de/nexus/content/repositories/angerona-releases"
echo Exit Code = %ERRORLEVEL%
if not "%ERRORLEVEL%" == "0" exit /b

call mvn deploy:deploy-file -Dfile="lib/net-sf-tweety-math/cream106.jar" -DgroupId="net.sf.tweety.dependencies" -DartifactId="cream" -Dversion="1.0.06" -Dpackaging="jar" -DrepositoryId="nexus-angerona" -Durl="http://ls1-www.cs.tu-dortmund.de/nexus/content/repositories/angerona-releases"
echo Exit Code = %ERRORLEVEL%
if not "%ERRORLEVEL%" == "0" exit /b

call mvn deploy:deploy-file -Dfile="lib/net-sf-tweety-math/qoca.jar" -DgroupId="net.sf.tweety.dependencies" -DartifactId="qoca" -Dversion="1.0.0" -Dpackaging="jar" -DrepositoryId="nexus-angerona" -Durl="http://ls1-www.cs.tu-dortmund.de/nexus/content/repositories/angerona-releases"
echo Exit Code = %ERRORLEVEL%
if not "%ERRORLEVEL%" == "0" exit /b

call mvn deploy:deploy-file -Dfile="lib/net-sf-tweety-graphs/Jama-1.0.3.jar" -DgroupId="net.sf.tweety.dependencies" -DartifactId="jama" -Dversion="1.0.3" -Dpackaging="jar" -DrepositoryId="nexus-angerona" -Durl="http://ls1-www.cs.tu-dortmund.de/nexus/content/repositories/angerona-releases"
echo Exit Code = %ERRORLEVEL%
if not "%ERRORLEVEL%" == "0" exit /b

call mvn deploy:deploy-file -Dfile="lib/net-sf-tweety-cli/jspf.core-1.0.2.jar" -DgroupId="net.sf.tweety.dependencies" -DartifactId="jspf" -Dversion="1.0.2" -Dpackaging="jar" -DrepositoryId="nexus-angerona" -Durl="http://ls1-www.cs.tu-dortmund.de/nexus/content/repositories/angerona-releases"
echo Exit Code = %ERRORLEVEL%
if not "%ERRORLEVEL%" == "0" exit /b

call mvn deploy:deploy-file -Dfile="lib/net-sf-tweety-math/lpsolve55j.jar" -DgroupId="net.sf.tweety.dependencies" -DartifactId="lpsolve" -Dversion="5.5" -Dpackaging="jar" -DrepositoryId="nexus-angerona" -Durl="http://ls1-www.cs.tu-dortmund.de/nexus/content/repositories/angerona-releases"
echo Exit Code = %ERRORLEVEL%
if not "%ERRORLEVEL%" == "0" exit /b

call mvn deploy:deploy-file -Dfile="lib/net-sf-tweety-math/ojalgo-35.0.jar" -DgroupId="net.sf.tweety.dependencies" -DartifactId="ojalgo" -Dversion="35" -Dpackaging="jar" -DrepositoryId="nexus-angerona" -Durl="http://ls1-www.cs.tu-dortmund.de/nexus/content/repositories/angerona-releases"
echo Exit Code = %ERRORLEVEL%
if not "%ERRORLEVEL%" == "0" exit /b