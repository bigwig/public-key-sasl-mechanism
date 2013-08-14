require 'highline'

task :default => [:release]
    
task :gather_parameters do
  ui = HighLine.new
  @version  = ui.ask("Release version to prepare: ")
  @dev_version  = ui.ask("Next dev version to prepare: ")
  @gpg_password = ui.ask("GPG password (using default key): ") { |q| q.echo = "*" }
  @tag_name = "v#{@version.gsub(/\./, "_")}"
end

task :prepare => [:gather_parameters] do
  puts "Preparing release #{@version} with tag #{@tag_name}, next dev version will be #{@dev_version}"
  sh "mvn release:clean release:prepare -B -DpushChanges=true -DpreparationGoals=validate -DreleaseVersion=#{@version} -DdevelopmentVersion=#{@dev_version} -Dtag=#{@tag_name}"
end

task :release => :gather_parameters do
  puts "Performing release #{@version}"

  sh("rm -rf checkout")
  sh("mkdir -p checkout")

  chdir('checkout') do
    sh "git clone git@github.com:bigwig/public-key-sasl-mechanism.git"
    chdir('public-key-sasl-mechanism') do
      sh "git checkout #{@tag_name}"
      sh "mvn clean javadoc:jar source:jar install -Dmaven.test.skip=true -Djetty.skip=true"

      puts "Deploying to sonatype"

      url = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
      repoId = "sonatype-nexus-staging"

      sh "mvn gpg:sign-and-deploy-file -Dgpg.passphrase=#{@gpg_password} -Durl=#{url} -DrepositoryId=#{repoId} -DpomFile=pom.xml -Dfile=target/public-key-sasl-mechanism-#{@version}.jar"
      sh "mvn gpg:sign-and-deploy-file -Dgpg.passphrase=#{@gpg_password} -Durl=#{url} -DrepositoryId=#{repoId} -DpomFile=pom.xml -Dfile=target/public-key-sasl-mechanism-#{@version}-sources.jar -Dclassifier=sources"
      sh "mvn gpg:sign-and-deploy-file -Dgpg.passphrase=#{@gpg_password} -Durl=#{url} -DrepositoryId=#{repoId} -DpomFile=pom.xml -Dfile=target/public-key-sasl-mechanism-#{@version}-javadoc.jar -Dclassifier=javadoc"

    end
  end

end