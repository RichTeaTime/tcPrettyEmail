package prettyemailer.teamcity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import jetbrains.buildServer.serverSide.BuildStatisticsOptions;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.SRunningBuild;
import jetbrains.buildServer.serverSide.ShortStatistics;
import jetbrains.buildServer.serverSide.TestBlockBean;
import jetbrains.buildServer.serverSide.problems.BuildProblem;
import jetbrains.buildServer.util.TimePrinter;
import jetbrains.buildServer.vcs.SVcsModification;
import jetbrains.buildServer.vcs.SelectPrevBuildPolicy;

public class PrettyEmailContentBuilder {
	SRunningBuild sRunningBuild;
	SBuildServer sBuildServer;
	ShortStatistics shortStats;
	int maxTestsToLoad;

	public PrettyEmailContentBuilder(SRunningBuild sRunningBuild, SBuildServer server, int maxTestsToLoad) {
		this.sRunningBuild = sRunningBuild;
		this.sBuildServer = server;
		this.maxTestsToLoad = maxTestsToLoad;
		BuildStatisticsOptions options = new BuildStatisticsOptions();
		options.setMaxNumberOfTestsStacktracesToLoad(this.maxTestsToLoad);
		options.setLoadCompilationErrors(false);
		this.shortStats = this.sRunningBuild.getBuildStatistics(options);
	}

	public List<TestBlockBean> getTests() {
		return this.shortStats.getFailedTests();
	}
	
	public List<SVcsModification> getChanges(){
		return sRunningBuild.getChanges(
				SelectPrevBuildPolicy.SINCE_LAST_SUCCESSFULLY_FINISHED_BUILD,
				true);
	}
	
	public List<BuildProblem> getBuildProblems(){
		return sRunningBuild.getBuildProblems();
	}

	public int getFailedTestCount(){
		return shortStats.getFailedTestCount();
	}

	public int getMaxTestCount(){
		return this.maxTestsToLoad;
	}	
	
	public int getNewFailedTestCount(){
		return shortStats.getNewFailedCount();
	}
	
	public int getPassedTestCount(){
		return shortStats.getPassedTestCount();
	}
	
	public int getIgnoredTestCount(){
		return shortStats.getIgnoredTestCount();
	}
	
	public String getProjectName(){
		return this.sBuildServer.getProjectManager().findProjectById(this.sRunningBuild.getProjectId()).getName();
	}
	
	public String getBuildTypeName(){
		return this.sRunningBuild.getBuildTypeName();
	}
	
	public String getProjectId(){
		return this.sRunningBuild.getProjectId();
	}
	
	public String getBuildTypeId(){
		return this.sRunningBuild.getBuildTypeId();
	}	
	public String getBuildNumber(){
		return this.sRunningBuild.getBuildNumber();
	}
	
	public String getTriggeredBy(){
		return this.sRunningBuild.getTriggeredBy().getAsString();
	}
	
	public String getRootURL(){
		return this.sBuildServer.getRootUrl();
	}
	
	public String getAgentName(){
		return this.sRunningBuild.getAgentName();
	}

	public String getStatus(){
		return this.sRunningBuild.getStatusDescriptor().getText();
	}

	public String getStatusLowercase(){
		return this.getStatus().toLowerCase();
	}
	
	public String getDate(){
		Calendar calFinishDate = Calendar.getInstance();
		calFinishDate.setTime(this.sRunningBuild.getStartDate());
		calFinishDate.add(Calendar.SECOND, (int) this.sRunningBuild.getDuration());
		
		final StringBuilder sb = new StringBuilder();
		TimePrinter.createSecondsFormatter(false).formatTime(sb, this.sRunningBuild.getDuration());
		
		System.out.println("getStartDate() : " + this.sRunningBuild.getStartDate());
		System.out.println("getFinishDate() : " + this.sRunningBuild.getFinishDate());
		System.out.println("Duration() : " + this.sRunningBuild.getDuration());
		SimpleDateFormat startDateFormat = new SimpleDateFormat("dd MMM yy HH:mm");
		SimpleDateFormat endDateFormat = new SimpleDateFormat("HH:mm");
		return startDateFormat.format(this.sRunningBuild.getStartDate())
			+ " - "
			+ endDateFormat.format(calFinishDate.getTime())
			+ " (" + sb.toString() + ")";
	}
	
}
