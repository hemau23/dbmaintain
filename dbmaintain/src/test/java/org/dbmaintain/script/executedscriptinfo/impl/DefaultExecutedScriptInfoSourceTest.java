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
package org.dbmaintain.script.executedscriptinfo.impl;

import org.dbmaintain.database.Database;
import org.dbmaintain.script.ExecutedScript;
import org.dbmaintain.script.Script;
import org.dbmaintain.util.DbMaintainException;
import org.dbmaintain.util.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.text.ParseException;
import java.util.Set;

import static junit.framework.Assert.*;
import static org.apache.commons.lang.time.DateUtils.parseDate;
import static org.dbmaintain.util.SQLTestUtils.executeUpdate;
import static org.dbmaintain.util.SQLTestUtils.executeUpdateQuietly;
import static org.dbmaintain.util.TestUtils.createScript;

/**
 * Test class for {@link org.dbmaintain.script.executedscriptinfo.impl.DefaultExecutedScriptInfoSource}. The implementation is tested using a
 * test database. The dbms that is used depends on the database configuration in test/resources/unitils.properties
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DefaultExecutedScriptInfoSourceTest {

    /* The tested instance */
    private DefaultExecutedScriptInfoSource executedScriptInfoSource;
    /* The tested instance with auto-create configured */
    private DefaultExecutedScriptInfoSource executedScriptInfoSourceAutoCreate;

    private DataSource dataSource;
    private Database defaultDatabase;

    private ExecutedScript executedScript1;
    private ExecutedScript executedScript2;
    private ExecutedScript executedPostprocessingScript;


    @Before
    public void initialize() {
        defaultDatabase = TestUtils.getDatabases().getDefaultDatabase();
        dataSource = defaultDatabase.getDataSource();

        initExecutedScriptInfoSource();

        dropExecutedScriptsTable();
        createExecutedScriptsTable();
    }

    private void initExecutedScriptInfoSource() {
        executedScriptInfoSource = TestUtils.getDefaultExecutedScriptInfoSource(defaultDatabase, false);
        executedScriptInfoSourceAutoCreate = TestUtils.getDefaultExecutedScriptInfoSource(defaultDatabase, true);
    }

    @Before
    public void initTestData() throws ParseException {
        executedScript1 = new ExecutedScript(createScript("1_script1.sql"), parseDate("20/05/2008 10:20:00", new String[]{"dd/MM/yyyy hh:mm:ss"}), false);
        executedScript2 = new ExecutedScript(createScript("script2.sql"), parseDate("20/05/2008 10:25:00", new String[]{"dd/MM/yyyy hh:mm:ss"}), false);
        executedPostprocessingScript = new ExecutedScript(createScript("postprocessing/postprocessingscript1.sql"), parseDate("20/05/2008 10:25:00", new String[]{"dd/MM/yyyy hh:mm:ss"}), false);
    }

    @After
    public void cleanUp() {
        dropExecutedScriptsTable();
    }


    @Test
    public void registerAndRetrieveExecutedScript() {
        executedScriptInfoSource.registerExecutedScript(executedScript1);
        assertEquals(1, executedScriptInfoSource.getExecutedScripts().size());
        assertTrue(executedScriptInfoSource.getExecutedScripts().contains(executedScript1));
        initExecutedScriptInfoSource();
        assertEquals(1, executedScriptInfoSource.getExecutedScripts().size());
        assertTrue(executedScriptInfoSource.getExecutedScripts().contains(executedScript1));

        executedScriptInfoSource.registerExecutedScript(executedScript2);
        Set<ExecutedScript> executedScripts2 = executedScriptInfoSource.getExecutedScripts();
        assertEquals(2, executedScripts2.size());
        assertTrue(executedScripts2.contains(executedScript1));
        assertTrue(executedScripts2.contains(executedScript2));
        initExecutedScriptInfoSource();
        assertEquals(2, executedScripts2.size());
        assertTrue(executedScripts2.contains(executedScript1));
        assertTrue(executedScripts2.contains(executedScript2));
    }

    @Test(expected = DbMaintainException.class)
    public void registerExecutedScript_NoExecutedScriptsTable() {
        dropExecutedScriptsTable();
        executedScriptInfoSource.registerExecutedScript(executedScript1);
    }

    @Test
    public void autoCreateExecutedScriptsTable() {
        dropExecutedScriptsTable();

        executedScriptInfoSourceAutoCreate.registerExecutedScript(executedScript1);
        assertEquals(executedScript1, executedScriptInfoSource.getExecutedScripts().first());
        initExecutedScriptInfoSource();
        assertEquals(executedScript1, executedScriptInfoSource.getExecutedScripts().first());
    }

    @Test
    public void updateExecutedScript() {
        executedScriptInfoSource.registerExecutedScript(executedScript1);
        assertFalse(executedScriptInfoSource.getExecutedScripts().first().isSuccessful());
        executedScript1.setSuccessful(true);
        executedScriptInfoSource.updateExecutedScript(executedScript1);
        assertTrue(executedScriptInfoSource.getExecutedScripts().first().isSuccessful());
        initExecutedScriptInfoSource();
        assertTrue(executedScriptInfoSource.getExecutedScripts().first().isSuccessful());
    }

    @Test
    public void renameExecutedScript() {
        executedScriptInfoSource.registerExecutedScript(executedScript1);
        Script renamedToScript = createScript("1_script1_renamed.sql");
        executedScriptInfoSource.renameExecutedScript(executedScript1, renamedToScript);
        assertEquals(renamedToScript, executedScriptInfoSource.getExecutedScripts().first().getScript());
        initExecutedScriptInfoSource();
        assertEquals(renamedToScript, executedScriptInfoSource.getExecutedScripts().first().getScript());
    }

    @Test
    public void clearAllRegisteredScripts() {
        executedScriptInfoSource.registerExecutedScript(executedScript1);
        executedScriptInfoSource.registerExecutedScript(executedScript2);
        executedScriptInfoSource.clearAllExecutedScripts();
        assertEquals(0, executedScriptInfoSource.getExecutedScripts().size());
        initExecutedScriptInfoSource();
        assertEquals(0, executedScriptInfoSource.getExecutedScripts().size());
    }

    @Test
    public void deleteExecutedScript() {
        executedScriptInfoSource.registerExecutedScript(executedScript1);
        assertEquals(1, executedScriptInfoSource.getExecutedScripts().size());
        executedScriptInfoSource.deleteExecutedScript(executedScript1);
        assertEquals(0, executedScriptInfoSource.getExecutedScripts().size());
        initExecutedScriptInfoSource();
        assertEquals(0, executedScriptInfoSource.getExecutedScripts().size());
    }


    @Test
    public void deleteAllExecutedPostprocessingScripts() {
        executedScriptInfoSource.registerExecutedScript(executedScript1);
        executedScriptInfoSource.registerExecutedScript(executedPostprocessingScript);
        assertEquals(2, executedScriptInfoSource.getExecutedScripts().size());
        executedScriptInfoSource.deleteAllExecutedPostprocessingScripts();
        assertEquals(1, executedScriptInfoSource.getExecutedScripts().size());
        assertEquals(executedScript1, executedScriptInfoSource.getExecutedScripts().first());
        initExecutedScriptInfoSource();
        assertEquals(1, executedScriptInfoSource.getExecutedScripts().size());
        assertEquals(executedScript1, executedScriptInfoSource.getExecutedScripts().first());
    }


    private void createExecutedScriptsTable() {
        executeUpdate(executedScriptInfoSource.getCreateExecutedScriptTableStatement(), dataSource);
    }

    private void dropExecutedScriptsTable() {
        executeUpdateQuietly("drop table dbmaintain_scripts", dataSource);
    }

}
