package org.rest.sec.persistence.service;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rest.sec.model.Privilege;
import org.rest.sec.model.Role;
import org.rest.spring.context.ContextTestConfig;
import org.rest.spring.persistence.jpa.PersistenceJPAConfig;
import org.rest.spring.testing.TestingTestConfig;
import org.rest.util.IdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.google.common.collect.Sets;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( classes = { TestingTestConfig.class, PersistenceJPAConfig.class, ContextTestConfig.class },loader = AnnotationConfigContextLoader.class )
@SuppressWarnings( "unchecked" )
@TransactionConfiguration( defaultRollback = true )
public class RoleServiceSearchPersistenceIntegrationTest{
	private static final String ID = "id";
	private static final String NAME = "name";
	
	@Autowired private IPrivilegeService privilegeService;
	@Autowired private IRoleService roleService;
	@Autowired private IPrincipalService principalService;
	
	// fixtures
	
	@Before
	public final void before(){
		principalService.deleteAll();
		roleService.deleteAll();
		privilegeService.deleteAll();
	}
	
	// search/filter
	
	@Test
	public final void whenSearchByNameIsPerformed_thenNoExceptions(){
		final Role existingEntity = getService().create( createNewEntity() );
		
		// When
		final ImmutablePair< String, String > nameConstraint = new ImmutablePair< String, String >( NAME, existingEntity.getName() );
		getService().search( nameConstraint );
	}
	
	// search - by id
	
	@Test
	public final void givenEntityWithIdExists_whenSearchByIdIsPerformed_thenResultIsFound(){
		final Role existingEntity = getService().create( createNewEntity() );
		
		// When
		final ImmutablePair< String, Long > idConstraint = new ImmutablePair< String, Long >( ID, existingEntity.getId() );
		final List< Role > searchResults = getService().search( idConstraint );
		
		// Then
		assertThat( searchResults, hasItem( existingEntity ) );
	}
	@Test
	public final void givenEntityWithIdDoesNotExist_whenSearchByIdIsPerformed_thenResultIsNotFound(){
		final Role existingEntity = getService().create( createNewEntity() );
		
		// When
		final ImmutablePair< String, Long > idConstraint = new ImmutablePair< String, Long >( ID, IdUtil.randomPositiveLong() );
		final List< Role > searchResults = getService().search( idConstraint );
		
		// Then
		assertThat( searchResults, not( hasItem( existingEntity ) ) );
	}
	
	// search - by name
	
	@Test
	public final void givenEntityWithNameExists_whenSearchByNameIsPerformed_thenResultIsFound(){
		final Role existingEntity = getService().create( createNewEntity() );
		
		// When
		final ImmutablePair< String, String > nameConstraint = new ImmutablePair< String, String >( NAME, existingEntity.getName() );
		final List< Role > searchResults = getService().search( nameConstraint );
		
		// Then
		assertThat( searchResults, hasItem( existingEntity ) );
	}
	@Test
	public final void givenEntityWithNameDoesNotExist_whenSearchByNameIsPerformed_thenResultIsNotFound(){
		final Role existingEntity = getService().create( createNewEntity() );
		
		// When
		final ImmutablePair< String, String > nameConstraint = new ImmutablePair< String, String >( NAME, randomAlphabetic( 8 ) );
		final List< Role > searchResults = getService().search( nameConstraint );
		
		// Then
		assertThat( searchResults, not( hasItem( existingEntity ) ) );
	}
	
	@Test
	public final void givenEntitiesExists_whenSearchByNegatedNameIsPerformed_thenResultsAreCorrect(){
		final Role existingEntity1 = getService().create( createNewEntity() );
		final Role existingEntity2 = getService().create( createNewEntity() );
		
		// When
		final ImmutablePair< String, String > nameConstraint = new ImmutablePair< String, String >( NAME, existingEntity1.getName() );
		final List< Role > searchResults = getService().search( nameConstraint );
		
		// Then
		assertThat( searchResults, hasItem( existingEntity1 ) );
		assertThat( searchResults, not( hasItem( existingEntity2 ) ) );
	}
	
	// search - by id and name
	
	@Test
	public final void givenEntityExists_whenSearchIsPerformedByIdAndName_thenResultIsFound(){
		final Role existingEntity = getService().create( createNewEntity() );
		
		// When
		final ImmutablePair< String, String > nameConstraint = new ImmutablePair< String, String >( NAME, existingEntity.getName() );
		final ImmutablePair< String, Long > idConstraint = new ImmutablePair< String, Long >( ID, existingEntity.getId() );
		final List< Role > searchResults = getService().search( nameConstraint, idConstraint );
		
		// Then
		assertThat( searchResults, hasItem( existingEntity ) );
	}
	@Test
	public final void givenEntityExists_whenSearchIsPerformedByIncorrectIdAndCorrectName_thenResultIsNotFound(){
		final Role existingEntity = getService().create( createNewEntity() );
		
		// When
		final ImmutablePair< String, String > nameConstraint = new ImmutablePair< String, String >( NAME, existingEntity.getName() );
		final ImmutablePair< String, Long > idConstraint = new ImmutablePair< String, Long >( ID, IdUtil.randomPositiveLong() );
		final List< Role > searchResults = getService().search( nameConstraint, idConstraint );
		
		// Then
		assertThat( searchResults, not( hasItem( existingEntity ) ) );
	}
	@Test
	public final void givenEntityExists_whenSearchIsPerformedByCorrectIdAndIncorrectName_thenResultIsNotFound(){
		final Role existingEntity = getService().create( createNewEntity() );
		
		// When
		final ImmutablePair< String, String > nameConstraint = new ImmutablePair< String, String >( NAME, randomAlphabetic( 8 ) );
		final ImmutablePair< String, Long > idConstraint = new ImmutablePair< String, Long >( ID, existingEntity.getId() );
		final List< Role > searchResults = getService().search( nameConstraint, idConstraint );
		
		// Then
		assertThat( searchResults, not( hasItem( existingEntity ) ) );
	}
	
	// search - by negated id,name
	
	@Test
	public final void whenSearchByNegatedNameIsPerformed_thenResultsAreFound(){
		final Role existingEntity1 = getService().create( createNewEntity() );
		final Role existingEntity2 = getService().create( createNewEntity() );
		
		// When
		final ImmutablePair< String, String > nameConstraint = new ImmutablePair< String, String >( "~" + NAME, existingEntity1.getName() );
		final List< Role > searchResults = getService().search( nameConstraint );
		
		// Then
		assertThat( searchResults, not( hasItem( existingEntity1 ) ) );
		assertThat( searchResults, hasItem( existingEntity2 ) );
	}
	@Test
	public final void whenSearchByNegatedIdIsPerformed_thenResultsAreFound(){
		final Role existingEntity1 = getService().create( createNewEntity() );
		final Role existingEntity2 = getService().create( createNewEntity() );
		
		// When
		final ImmutablePair< String, Long > idConstraint = new ImmutablePair< String, Long >( "~" + ID, existingEntity1.getId() );
		final List< Role > searchResults = getService().search( idConstraint );
		
		// Then
		assertThat( searchResults, not( hasItem( existingEntity1 ) ) );
		assertThat( searchResults, hasItem( existingEntity2 ) );
	}
	
	// template method
	
	protected final IRoleService getService(){
		return roleService;
	}
	protected final Role createNewEntity(){
		return this.createNewEntity( randomAlphabetic( 8 ) );
	}
	protected final void invalidate( final Role entity ){
		entity.setName( null );
	}
	protected final void changeEntity( final Role entity ){
		entity.setName( randomAlphabetic( 6 ) );
	}
	
	// util
	
	protected final Role createNewEntity( final String name ){
		return new Role( name, Sets.<Privilege> newHashSet() );
	}
	
	final IPrivilegeService getAssociationService(){
		return privilegeService;
	}
	
}
