/*
 * Copyright DbMaintain.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dbmaintain.launch.task;

import org.dbmaintain.MainFactory;
import org.dbmaintain.structure.clean.DBCleaner;

import java.util.List;

/**
 * Task that removes the data of all database tables, except for the DBMAINTAIN_SCRIPTS table.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class CleanDatabaseTask extends DbMaintainDatabaseTask {


    public CleanDatabaseTask() {
    }

    public CleanDatabaseTask(List<DbMaintainDatabase> taskDatabases) {
        super(taskDatabases);
    }


    @Override
    protected void addTaskConfiguration(TaskConfiguration taskConfiguration) {
        taskConfiguration.addDatabaseConfigurations(databases);
    }

    @Override
    protected boolean doExecute(MainFactory mainFactory) {
        DBCleaner dbCleaner = mainFactory.createDBCleaner();
        dbCleaner.cleanDatabase();
        return true;
    }

}
