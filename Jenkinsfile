pipeline {
	agent any  
	triggers {
		cron(env.BRANCH_NAME == 'master' ? '57 20 23 11 *' : '') //In the ternary operator give cron expression to schedule build Example: cron(env.BRANCH_NAME == 'master' ? '0 9 * * 4' : '') - Every Thursday at 9 AM
	}
	stages {
		stage('Init') {
			steps {
				script {			
						def envToDeploy = "TEST";
						def userInput = "No";
						
						if(currentBuild.rawBuild.getCause(hudson.triggers.TimerTrigger$TimerTriggerCause)) {
							env.IS_SCHEDULED_BUILD = 'TRUE';
						} else {
							env.IS_SCHEDULED_BUILD = 'FALSE';
						}
						
						// If this is scheduled build and running in prod jenkins, then deploy to prod env				
						if(env.BRANCH_NAME == 'master' && env.IS_SCHEDULED_BUILD == 'TRUE' && env.IS_PROD_JENKIN == 'TRUE') {
							envToDeploy = 'PROD';
						} else if(env.BRANCH_NAME == 'master' && env.IS_PROD_JENKIN == 'TRUE') {
							// If the current branch is master and running in prod jenkins, then wait for user input and decide
							try {
								timeout(time: 15, unit: 'SECONDS') {
									userInput = input(id: 'envInput', message: 'Are you sure you want to deploy to My I-MED production servers?',
									parameters: [[$class: 'ChoiceParameterDefinition', defaultValue: 'No', 
										description:'Environments to deploy', name:'nameChoice', choices: "No\nYes"]
									]);
									println("The user selected option " + userInput + " to deploy to My I-MED production servers");
								}							
							} catch(err) { // timeout reached or input false
								def user = err.getCauses()[0].getUser()
								if('SYSTEM' == user.toString()) { // SYSTEM means timeout.																
									abortCurrentBuild('User input timed out');
								} else {
									abortCurrentBuild('User aborted the build.');
								}
							}
							
							if(userInput == 'Yes') {
								envToDeploy = 'PROD';	
							} else {
								abortCurrentBuild('User selected No to deploy to My I-MED production servers');
							}							
						}
						
						env.ENV_TO_DEPLOY = envToDeploy; //Use this value to branch to different logic if needed..	
						
						println('Is it a scheduled build :' + env.IS_SCHEDULED_BUILD);
						println('Is it a prod jenkins :' + env.IS_PROD_JENKIN);
						println('Selected env to deploy :' + env.ENV_TO_DEPLOY);
					}
			}
		}
	
	
		stage('Build') {
			steps {
				script {
					if (env.ENV_TO_DEPLOY == 'PROD') {
						echo 'Going to build the project for prod environment.'
						dir(path: 'referrer-portal') {
						  sh 'mvn package -Pprod -Dmaven.test.skip=true'
						}						
					} else {
						echo 'Going to build the project for test environment.'
						dir(path: 'referrer-portal') {
						  sh 'mvn package -Ptest -Dmaven.test.skip=true'
						}						
					}
				}
			}
		}

		stage('Deploy') {
			environment {				
					DEPLY_CREDS = credentials("${env.ENV_TO_DEPLOY == 'PROD'?'prodrefportaltomcat':'testrefportaltomcat'}");
			}
			steps {
				script {
					def serversToDeploy = "${env.ENV_TO_DEPLOY == 'PROD'?'imedpdtom05,imedpdtom06,imedpdtom07,imedpdtom08':'imedtstom05,imedtstom06,imedtstom07,imedtstom08'}";
					
					println('Deploy UserId : ' + DEPLY_CREDS_USR);
					println('Deploy Servers: ' + serversToDeploy);
					
					//def usrcred = "${env.ENV_TO_DEPLOY == 'PROD'?'huehara:PR0DD3pl0ym3nt%X':'huehara:D3pl0ym3nt%'}";
					//echo 'deployment user credential ' + usrcred

					serversToDeploy.tokenize(',').each {						
						echo 'Going to deploy the application in the server :' + it

						dir(path: 'referrer-portal/target') {
							sh 'curl http://' + it + '.imed:8080/manager/text/undeploy?path=/referrer -u ' + DEPLY_CREDS_USR + ':' + DEPLY_CREDS_PSW
							sh 'curl -T referrer.war http://' + it + '.imed:8080/manager/text/deploy?path=/referrer -u ' + DEPLY_CREDS_USR + ':' + DEPLY_CREDS_PSW
						}
						echo 'Deployed the application in the server :' + it
					}
					echo 'Completed the deployment to test environment'

				}
			}
		}

	}
	
	post {
		cleanup {
			
			deleteDir()
			
			dir("${workspace}@tmp") {
				deleteDir()
			}
			
			dir("${workspace}@script") {
				deleteDir()
			}
		}
	}
  
	tools {
		maven 'MAVEN_TOOL'
		jdk 'JAVA'
	}
}

void abortCurrentBuild(String reason) {
	println(reason);
	currentBuild.result = 'ABORTED';
	error(reason);
}