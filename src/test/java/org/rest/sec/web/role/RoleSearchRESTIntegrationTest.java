package org.rest.sec.web.role;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rest.client.template.impl.ClientOperations;
import org.rest.client.template.impl.PrivilegeRESTTemplateImpl;
import org.rest.client.template.impl.RoleRESTTemplateImpl;
import org.rest.sec.model.Role;
import org.rest.spring.client.ClientTestConfig;
import org.rest.spring.context.ContextTestConfig;
import org.rest.spring.testing.TestingTestConfig;
import org.rest.util.IdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( classes = { ClientTestConfig.class, TestingTestConfig.class, ContextTestConfig.class },loader = AnnotationConfigContextLoader.class )
public class RoleSearchRESTIntegrationTest{
	
	@Autowired private RoleRESTTemplateImpl restTemplate;
	@Autowired private PrivilegeRESTTemplateImpl associationRestTemplate;
	
	public RoleSearchRESTIntegrationTest(){
		super();
	}
	
	// tests
	
	// search - by id
	
	@Test
	public final void givenResourceExists_whenResourceIfSearchedById_thenNoExceptions(){
		final Role existingResource = getTemplate().create( getTemplate().createNewEntity() );
		getTemplate().searchAsResponse( existingResource.getId(), null );
	}
	@Test
	public final void givenResourceExists_whenResourceIfSearchedById_then200IsReceived(){
		final Role existingResource = getTemplate().create( getTemplate().createNewEntity() );
		
		// When
		final Response searchResponse = getTemplate().searchAsResponse( existingResource.getId(), null );
		
		// Then
		assertThat( searchResponse.getStatusCode(), is( 200 ) );
	}
	
	@Test
	public final void givenResourceExists_whenResourceIfSearchedByIdAndUnmarshalled_thenNoException(){
		final Role existingResource = getTemplate().create( getTemplate().createNewEntity() );
		getTemplate().search( existingResource.getId(), null );
	}
	@Test
	public final void givenResourceExists_whenResourceIfSearchedByIdAndUnmarshalled_thenResourceIsFound(){
		final Role existingResource = getTemplate().create( getTemplate().createNewEntity() );
		
		// When
		final List< Role > found = getTemplate().search( existingResource.getId(), null );
		
		// Then
		assertThat( found, hasItem( existingResource ) );
	}
	
	// search - by name
	
	@Test
	public final void givenResourceExists_whenResourceIfSearchedByName_thenNoExceptions(){
		final Role existingResource = getTemplate().create( getTemplate().createNewEntity() );
		getTemplate().searchAsResponse( null, existingResource.getName() );
	}
	@Test
	public final void givenResourceExists_whenResourceIfSearchedByName_then200IsReceived(){
		final Role existingResource = getTemplate().create( getTemplate().createNewEntity() );
		
		// When
		final Response searchResponse = getTemplate().searchAsResponse( null, existingResource.getName() );
		
		// Then
		assertThat( searchResponse.getStatusCode(), is( 200 ) );
	}
	
	@Test
	public final void givenResourceExists_whenResourceIfSearchedByNameAndUnmarshalled_thenNoException(){
		final Role existingResource = getTemplate().create( getTemplate().createNewEntity() );
		getTemplate().search( null, existingResource.getName() );
	}
	@Test
	public final void givenResourceExists_whenResourceIfSearchedByNameAndUnmarshalled_thenResourceIsFound(){
		final Role existingResource = getTemplate().create( getTemplate().createNewEntity() );
		
		// When
		final List< Role > found = getTemplate().search( null, existingResource.getName() );
		
		// Then
		assertThat( found, hasItem( existingResource ) );
	}
	
	// search - by id and name
	
	@Test
	public final void givenResourceExists_whenResourceIfSearchedByIdAndName_then200IsReceived(){
		final Role existingResource = getTemplate().create( getTemplate().createNewEntity() );
		
		// When
		final Response searchResponse = getTemplate().searchAsResponse( existingResource.getId(), existingResource.getName() );
		
		// Then
		assertThat( searchResponse.getStatusCode(), is( 200 ) );
	}
	@Test
	public final void givenResourceExists_whenResourceIfSearchedByIdAndNameAndUnmarshalled_thenResourceIsFound(){
		final Role existingResource = getTemplate().create( getTemplate().createNewEntity() );
		
		// When
		final List< Role > found = getTemplate().search( existingResource.getId(), existingResource.getName() );
		
		// Then
		assertThat( found, hasItem( existingResource ) );
	}
	@Test
	public final void givenResourceExists_whenResourceIfSearchedByIdAndWrongNameAndUnmarshalled_thenResourceIsFound(){
		final Role existingResource = getTemplate().create( getTemplate().createNewEntity() );
		
		// When
		final List< Role > found = getTemplate().search( existingResource.getId(), randomAlphabetic( 8 ) );
		
		// Then
		assertThat( found, not( hasItem( existingResource ) ) );
	}
	@Test
	public final void givenResourceExists_whenResourceIfSearchedByWrongIdAndCorrectNameAndUnmarshalled_thenResourceIsFound(){
		final Role existingResource = getTemplate().create( getTemplate().createNewEntity() );
		
		// When
		final List< Role > found = getTemplate().search( IdUtil.randomPositiveLong(), existingResource.getName() );
		
		// Then
		assertThat( found, not( hasItem( existingResource ) ) );
	}
	@Test
	public final void givenResourceExists_whenResourceIfSearchedByWrongIdAndWrongNameAndUnmarshalled_thenResourceIsFound(){
		final Role existingResource = getTemplate().create( getTemplate().createNewEntity() );
		
		// When
		final List< Role > found = getTemplate().search( IdUtil.randomPositiveLong(), randomAlphabetic( 8 ) );
		
		// Then
		assertThat( found, not( hasItem( existingResource ) ) );
	}
	
	// search - by negated id,name
	
	@Test
	public final void givenResourceExists_whenResourceIfSearchedByNegatedName_then200IsReceived(){
		final Role existingResource = getTemplate().create( getTemplate().createNewEntity() );
		
		final ImmutablePair< String, ClientOperations > negatedNameConstraint = new ImmutablePair< String, ClientOperations >( existingResource.getName(), ClientOperations.NEG_EQ );
		
		// When
		final Response searchResponse = getTemplate().searchAsResponse( null, negatedNameConstraint );
		
		// Then
		assertThat( searchResponse.getStatusCode(), is( 200 ) );
	}
	
	@Test
	public final void givenResourceExists_whenResourceIfSearchedByNegatedId_then200IsReceived(){
		final Role existingResource = getTemplate().create( getTemplate().createNewEntity() );
		
		final ImmutablePair< Long, ClientOperations > negatedIdConstraint = new ImmutablePair< Long, ClientOperations >( existingResource.getId(), ClientOperations.NEG_EQ );
		
		// When
		final Response searchResponse = getTemplate().searchAsResponse( negatedIdConstraint, null );
		
		// Then
		assertThat( searchResponse.getStatusCode(), is( 200 ) );
	}
	
	// template
	
	protected final Role createNewEntity(){
		return restTemplate.createNewEntity();
	}
	protected final RoleRESTTemplateImpl getTemplate(){
		return restTemplate;
	}
	protected final String getURI(){
		return getTemplate().getURI() + "/";
	}
	protected final void change( final Role resource ){
		resource.setName( randomAlphabetic( 6 ) );
	}
	protected final void invalidate( final Role resource ){
		getTemplate().invalidate( resource );
	}
	protected final RequestSpecification givenAuthenticated(){
		return getTemplate().givenAuthenticated();
	}
	
	// util
	
	final PrivilegeRESTTemplateImpl getAssociationTemplate(){
		return associationRestTemplate;
	}
	
}
