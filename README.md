
DbMaintain enables automatic roll-out of updates to a relational database. It brings database scripts into version control just like regular source code and can be used to transparently deploy databases from development to production. DbMaintain automates the part of the deployment which is most difficult to automate, opening the door for fully automated deployments in all stages of development.  You no longer have to keep track of which database updates have been deployed on which database - DbMaintain does this for you. The database itself keeps track of the scripts that have been applied. A checksum is stored for each script that to ensure nothing has changed to the scripts that were already applied.  Updates are performed incrementally: Only what has been changed since the last deployment is applied. Features such as repeatable scripts, postprocessing scripts, multi-database / database user support and support for patches turn DbMaintain into a complete solution for the enterprise.

The original code has been checked out from http://www.dbmaintain.org/overview.html.

So whats changed in this version. 

1. The original db mainetain script show success with follwoing query which should be ideally failed because "test" table is not exist 

Should get error :
Msg 208, Level 16, State 1, Line 1
Invalid object name 'test'.

Query :

select 1
insert into test (1,2) ;

With this version everything is working fine.


Build and Installation .

1. download source code :

git clone https://github.com/hemau23/dbmaintain.git

2. download gpg  from http://gpg4win.org/download.html and install it

3. run gpg --gen-key and create key 

you can skip above step 2 and 3 by just removing following plugin from root pom.xml

 <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-gpg-plugin</artifactId>
                    <executions>
                        <execution>
                           <id>sign-artifacts</id>
                           <phase>verify</phase>
                           <goals>
                              <goal>sign</goal>
                           </goals>
                        </execution>
                    </executions>
	       </plugin>
	       
	       
	       
	 4. run mvn clean install
	       

 How to use dbmaintain 
 
 1. go to http://www.dbmaintain.org/overview.html.



