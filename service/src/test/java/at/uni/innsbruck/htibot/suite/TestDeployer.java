package com.mpulso.srp.suite;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.eu.ingwar.tools.arquillian.extension.suite.annotations.ArquillianSuiteDeployment;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.FileAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;

@ArquillianSuiteDeployment
public final class TestDeployer {

  private TestDeployer() {
    //static access only
  }

  @Deployment
  public static WebArchive createDeployment() {

    final File[] dependencies = Maven.resolver()
                                     .loadPomFromFile("./pom.xml")
                                     .importDependencies(ScopeType.COMPILE, ScopeType.TEST)
                                     .resolve()
                                     .withTransitivity()
                                     .asFile();

    final WebArchive webArchive = ShrinkWrap.create(WebArchive.class, "arquillian-it.war")
                                            .addAsResource(new File("target/classes/com"), "com/")
                                            .addAsResource(new File("target/classes/org"), "org/")
                                            .addAsResource(new File("target/test-classes/com"), "com/")
                                            .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                                            .addAsLibraries(dependencies)
                                            .addManifest();

    return TestDeployer.addAllFiles(webArchive, "src/main/webapp", "/");

  }

  private static WebArchive addAllFiles(final WebArchive webArchive, String directory, String target) {
    try {
      if (directory.endsWith("/")) {
        directory = directory.substring(0, target.length() - 1);
      }

      if (target.endsWith("/")) {
        target = target.substring(0, target.length() - 1);
      }

      final File sourceDir = new File(directory);

      final List<Path> allFiles = Files.walk(sourceDir.toPath())
                                       .filter(Files::isRegularFile)
                                       .filter(path -> !path.toString().contains(".git"))
                                       .collect(Collectors.toList());

      final Pattern cutToRelativePath = Pattern.compile("(.*?)" + Pattern.quote(directory));

      for (final Path path : allFiles) {
        final String compatiblePath = path.toString().replaceAll("\\\\", "/"); // windows compatiblity, or regex will not work
        final String targetPath = target + cutToRelativePath.matcher(compatiblePath).replaceAll("");

        webArchive.add(new FileAsset(path.toFile()), targetPath);
      }
    } catch (final IOException e) {
      e.printStackTrace();
    }

    return webArchive;
  }

}
