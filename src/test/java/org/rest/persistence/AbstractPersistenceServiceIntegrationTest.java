package org.rest.persistence;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;
import org.rest.common.IEntity;
import org.rest.persistence.service.IService;
import org.rest.util.IdUtil;
import org.springframework.dao.DataAccessException;
import org.springframework.test.annotation.Rollback;

public abstract class AbstractPersistenceServiceIntegrationTest< T extends IEntity >{
	
	// tests
	
	// find - findAll
	
	@Test
	public void whenEntitiesAreRetrieved_thenNoExceptions(){
		getService().findAll();
	}
	@Test
	public void whenEntitiesAreRetrieved_thenTheResultIsNotNull(){
		final List< T > entities = getService().findAll();
		
		assertNotNull( entities );
	}
	@Test
	public void givenAnEntityExists_whenEntitiesAreRetrieved_thenThereIsAtLeastOneEntity(){
		persistNewEntity();
		
		final List< T > owners = getService().findAll();
		
		assertThat( owners, Matchers.not( Matchers.<T> empty() ) );
	}
	@Test
	public void givenAnEntityExists_whenEntitiesAreRetrieved_thenTheExistingEntityIsIndeedAmongThem(){
		final T existingEntity = persistNewEntity();
		
		final List< T > owners = getService().findAll();
		
		assertThat( owners, hasItem( existingEntity ) );
	}
	
	// find one
	
	@Test
	public void givenEntityExists_whenEntityIsRetrieved_thenNoExceptions(){
		final T existingEntity = persistNewEntity();
		getService().findOne( existingEntity.getId() );
	}
	@Test
	public void givenEntityDoesNotExist_whenEntityIsRetrieved_thenNoExceptions(){
		getService().findOne( IdUtil.randomPositiveLong() );
	}
	@Test
	public void givenEntityExists_whenEntityIsRetrieved_thenTheResultIsNotNull(){
		final T existingEntity = persistNewEntity();
		final T retrievedEntity = getService().findOne( existingEntity.getId() );
		assertNotNull( retrievedEntity );
	}
	@Test
	public void givenEntityDoesNotExist_whenEntityIsRetrieved_thenTheResultIsNull(){
		final T retrievedEntity = getService().findOne( IdUtil.randomPositiveLong() );
		assertNull( retrievedEntity );
	}
	@Test
	public void givenEntityExists_whenEntityIsRetrieved_thenEntityIsRetrievedCorrectly(){
		final T existingEntity = persistNewEntity();
		final T retrievedEntity = getService().findOne( existingEntity.getId() );
		assertEquals( existingEntity, retrievedEntity );
	}
	
	// create
	
	@Test( expected = RuntimeException.class )
	public void whenNullEntityIsCreated_thenException(){
		getService().create( null );
	}
	@Test( expected = DataAccessException.class )
	@Ignore( "Hibernate simply ignores the id silently and still saved (tracking this)" )
	public void whenEntityWithIdIsCreated_thenDataAccessException(){
		final T entityWithId = createNewEntity();
		entityWithId.setId( IdUtil.randomPositiveLong() );
		
		getService().create( entityWithId );
	}
	@Test
	@Rollback
	public void whenEntityIsCreated_thenNoExceptions(){
		persistNewEntity();
	}
	@Test
	public void whenEntityIsCreated_thenEntityIsRetrievable(){
		final T existingEntity = persistNewEntity();
		
		assertNotNull( getService().findOne( existingEntity.getId() ) );
	}
	@Test
	public void whenEntityIsCreated_thenSavedEntityIsEqualToOriginalEntity(){
		final T originalEntity = createNewEntity();
		final T savedEntity = getService().create( originalEntity );
		
		assertEquals( originalEntity, savedEntity );
	}
	
	@Test( expected = DataAccessException.class )
	public void whenEntityWithFailedConstraintsIsCreated_thenException(){
		final T invalidEntity = createNewEntity();
		invalidate( invalidEntity );
		
		getService().create( invalidEntity );
	}
	
	// update
	
	@Test( expected = RuntimeException.class )
	public void whenNullEntityIsUpdated_thenException(){
		getService().update( null );
	}
	@Test
	public void whenEntityIsUpdated_thenNoExceptions(){
		final T existingEntity = persistNewEntity();
		
		getService().update( existingEntity );
	}
	@Test( expected = DataAccessException.class )
	public void whenEntityIsUpdatedWithFailedConstraints_thenException(){
		final T existingEntity = persistNewEntity();
		invalidate( existingEntity );
		
		getService().update( existingEntity );
	}
	@Test
	public void whenEntityIsUpdated_thenTheUpdatedAreCorrectlyPersisted(){
		final T existingEntity = persistNewEntity();
		changeEntity( existingEntity );
		
		getService().update( existingEntity );
		
		final T updatedEntity = getService().findOne( existingEntity.getId() );
		assertEquals( existingEntity, updatedEntity );
	}
	
	// delete
	
	@Test( expected = DataAccessException.class )
	public void givenEntityDoesNotExists_whenEntityIsDeleted_thenDataAccessException(){
		// When
		getService().delete( IdUtil.randomPositiveLong() );
	}
	@Test( expected = DataAccessException.class )
	public void whenEntityIsDeletedByNegativeId_thenDataAccessException(){
		// When
		getService().delete( ( IdUtil.randomNegativeLong() ) );
	}
	@Test
	public void givenEntityExists_whenEntityIsDeleted_thenNoExceptions(){
		// Given
		final T existingLocation = persistNewEntity();
		
		// When
		getService().delete( existingLocation.getId() );
	}
	@Test
	public void givenEntityExists_whenEntityIsDeleted_thenEntityIsNoLongerRetrievable(){
		// Given
		final T existingEntity = persistNewEntity();
		
		// When
		getService().delete( existingEntity.getId() );
		
		// Then
		assertNull( getService().findOne( existingEntity.getId() ) );
	}
	
	// delete all
	
	@Test
	public void givenEntityExists_whenEntitiesAreDeleted_thenNoException(){
		// Given
		persistNewEntity();
		
		// When
		getService().deleteAll();
	}
	@Test
	public void givenNoEntityExists_whenEntitiesAreDeleted_thenNoException(){
		// Given
		getService().deleteAll();
		
		// When
		getService().deleteAll();
	}
	@Test
	public void givenEntityExists_whenEntitiesAreDeleted_thenNoEntitiesExist(){
		// Given
		persistNewEntity();
		
		// When
		getService().deleteAll();
		
		// Then
		assert ( getService().findAll().size() == 0 );
	}
	
	// template method
	
	protected abstract IService< T > getService();
	
	protected abstract T createNewEntity();
	
	protected abstract void invalidate( final T entity );
	
	protected abstract void changeEntity( final T entity );
	
	//
	
	protected T persistNewEntity(){
		return this.getService().create( this.createNewEntity() );
	}
	
}
