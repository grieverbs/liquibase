package liquibase.sqlgenerator.core;

import static org.junit.Assert.*;

import liquibase.RuntimeEnvironment;
import liquibase.database.core.SybaseDatabase;
import liquibase.executor.ExecutionOptions;
import liquibase.action.Sql;
import liquibase.statement.core.GetViewDefinitionStatement;

import org.junit.Test;

public class GetViewDefinitionGeneratorSybaseTest {

	@Test
	public void testGenerateSqlForDefaultSchema() {
		GetViewDefinitionGeneratorSybase generator = new GetViewDefinitionGeneratorSybase();
		GetViewDefinitionStatement statement = new GetViewDefinitionStatement(null, null, "view_name");
		Sql[] sql = generator.generateSql(statement, new ExecutionOptions(new RuntimeEnvironment(new SybaseDatabase())), null);
		assertEquals(1, sql.length);
		assertEquals("select text from syscomments where id = object_id('dbo.view_name') order by colid", sql[0].toSql());
	}
	
	@Test
	public void testGenerateSqlForNamedSchema() {
		GetViewDefinitionGeneratorSybase generator = new GetViewDefinitionGeneratorSybase();
		GetViewDefinitionStatement statement = new GetViewDefinitionStatement(null, "owner", "view_name");
		Sql[] sql = generator.generateSql(statement, new ExecutionOptions(new RuntimeEnvironment(new SybaseDatabase())), null);
		assertEquals(1, sql.length);
		assertEquals("select text from syscomments where id = object_id('OWNER.view_name') order by colid", sql[0].toSql());
	}

}
