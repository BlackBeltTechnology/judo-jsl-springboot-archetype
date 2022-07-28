#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import ${package}.${modelName.toLowerCase()}.sdk.${modelName.toLowerCase()}.${modelName.toLowerCase()}.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ${modelName}SpringApplicationTests {

	@Autowired
	Person.PersonDao personDao;

	@Test
	void testDaoFunctions() {
		Person createdPerson = personDao.create(Person.builder()
				.withFirstName("FirstName")
				.withLastName("LastName")
				.build());

		assertEquals(Optional.of("FirstName"), createdPerson.getFirstName());
		assertEquals(Optional.of("LastName"), createdPerson.getLastName());
		// Test derived
		assertEquals(Optional.of("FirstName LastName"), createdPerson.getFullName());
	}

}
