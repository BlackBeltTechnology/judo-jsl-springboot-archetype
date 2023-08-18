#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

/*-
 * #%L
 * JUDO JSL :: Springboot Archetype
 * %%
 * Copyright (C) 2018 - 2023 BlackBelt Technology
 * %%
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 * #L%
 */

import ${groupId}.api.${modelName.toLowerCase()}.${modelName.toLowerCase()}.person.Person;
import ${groupId}.api.${modelName.toLowerCase()}.${modelName.toLowerCase()}.person.PersonDao;
import ${groupId}.spring.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.ComponentScan;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ${applicationName}Tests {

    @Autowired
    PersonDao personDao;

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
