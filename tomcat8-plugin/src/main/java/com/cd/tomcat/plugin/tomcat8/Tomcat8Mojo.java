package com.cd.tomcat.plugin.tomcat8;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.core.AprLifecycleListener;
import org.apache.catalina.core.StandardServer;
import org.apache.catalina.startup.Tomcat;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.codehaus.plexus.util.FileUtils;

@Mojo(name = "run", requiresDependencyResolution = ResolutionScope.TEST, threadSafe = true)
@Execute(phase = LifecyclePhase.PROCESS_CLASSES)
public class Tomcat8Mojo extends AbstractMojo
{
	
	Logger logger = Logger.getLogger(Tomcat8Mojo.class.getName());
	
	@Parameter(defaultValue = "${project.artifacts}", required = true, readonly = true)
	private Set<Artifact> dependencies;
	
	@Parameter(defaultValue = "${basedir}/src/main/webapp", property = "warSourceDirectory")
	private File warSourceDirectory;
	
	@Parameter(defaultValue = "${project.build.outputDirectory}", property = "target/classes")
	private File targetClasses;
	
	@Parameter(property = "maven.tomcat.port", defaultValue = "8080")
	private int port;
	
	@Parameter(property = "maven.tomcat.path", defaultValue = "/")
	private String path;
	
	private static final Object lock = new Object();
	
	private Tomcat tomcat;
	
	private void startTomcat(int port, String contextPath, String docBase) throws ServletException, LifecycleException
	{
		tomcat = new Tomcat();
		tomcat.setPort(port);
		tomcat.setBaseDir(".");
		StandardServer server = (StandardServer) tomcat.getServer();
		AprLifecycleListener listener = new AprLifecycleListener();
		server.addLifecycleListener(listener);
		tomcat.addWebapp(contextPath, docBase);
		tomcat.start();
	}
	
	public void stopTomcat() throws LifecycleException
	{
		tomcat.stop();
	}
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException
	{
		File libDir = new File(warSourceDirectory, "WEB-INF/lib");
		File classDir = new File(warSourceDirectory, "WEB-INF/classes");
		
		deleteDir(libDir.getAbsolutePath());
		deleteDir(classDir.getAbsolutePath());
		
		if (!libDir.exists()) libDir.mkdirs();
		if (!classDir.exists()) classDir.mkdirs();
		
		try
		{
			copyFiles(targetClasses.getAbsolutePath(), classDir.getAbsolutePath());
		}
		catch (Exception e)
		{
			logger.log(Level.WARNING, "copy classes error !");
		}
		
		for (Artifact artifact : dependencies)
		{
			String scope = artifact.getScope();
			if (Artifact.SCOPE_RUNTIME.equals(scope) || Artifact.SCOPE_COMPILE.equals(scope))
			{
				try
				{
					FileUtils.copyFile(artifact.getFile(), new File(libDir, artifact.getArtifactId() + "-" + artifact.getVersion() + ".jar"));
				}
				catch (IOException e)
				{
					logger.log(Level.WARNING, "copy jar error !");
				}
			}
		}
		
		try
		{
			startTomcat(port, path, warSourceDirectory.getAbsolutePath());
		}
		catch (Exception e)
		{
			logger.log(Level.WARNING, "start tomcat error !");
		}
		
		try
		{
			synchronized (lock)
			{
				lock.wait();
			}
		}
		catch (InterruptedException e)
		{
		}
		
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					synchronized (lock)
					{
						lock.notifyAll();
					}
				}
				catch (Exception e)
				{
				}
			}
		}));
	}
	
	public void deleteDir(String path)
	{
		File f = new File(path);
		if (f.isDirectory())
		{
			File[] fs = f.listFiles();
			for (File file : fs)
			{
				this.deleteDir(file.toString());
				file.delete();
			}
		}
		else
		{
			f.delete();
		}
		f.delete();
	}
	
	public void copyFiles(String srcDir, String tarDir) throws Exception
	{
		File file = new File(srcDir);
		if (file.isDirectory())
		{
			File fx = new File(tarDir);
			if (!fx.exists()) fx.mkdir();
			File[] files = file.listFiles();
			for (File fy : files)
			{
				copyFiles(fy.toString(), tarDir + "/" + fy.getName());
			}
		}
		else
		{
			FileUtils.copyFile(new File(srcDir), new File(tarDir));
		}
	}
}
