package io.github.tf-govstack.registration.test.util.mastersync;

import static io.github.tf-govstack.registration.util.mastersync.MapperUtils.map;
import static io.github.tf-govstack.registration.util.mastersync.MetaDataUtils.setCreateMetaData;
import static io.github.tf-govstack.registration.util.mastersync.MetaDataUtils.setUpdateMetaData;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.github.tf-govstack.registration.context.SessionContext;
import io.github.tf-govstack.registration.context.SessionContext.UserContext;
import io.github.tf-govstack.registration.dto.mastersync.LanguageDto;
import io.github.tf-govstack.registration.entity.Language;

/**
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 */
@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*"})
@PrepareForTest({ SessionContext.class })
public class MapperTest {
	


	@Before
	public void setup() throws Exception{
		UserContext userContext = Mockito.mock(SessionContext.UserContext.class);
		PowerMockito.mockStatic(SessionContext.class);
		PowerMockito.doReturn(userContext).when(SessionContext.class, "userContext");
		PowerMockito.when(SessionContext.userContext().getUserId()).thenReturn("mosip");

	}

	@Test(expected = NullPointerException.class)
	public void testMapSourceNull() {
		map(null, new Language());
	}


	@Test(expected = NullPointerException.class)
	public void testMapMetaDataDestinationNull() {
		setCreateMetaData(new LanguageDto(), null);
	}


	@Test
	public void testSetUpdateMetaData() {
		LanguageDto dto = new LanguageDto();
		Language entity = new Language();

		dto.setCode("ENG");
		dto.setFamily("English");

		entity.setCode("eng");
		entity.setFamily("english");
		entity.setName("english");
		entity.setNativeName("english");
		entity.setIsActive(true);
		entity.setCrDtime(Timestamp.valueOf(LocalDateTime.now()));
		entity.setCrBy("admin");
		entity.setUpdBy("admin");

		setUpdateMetaData(dto, entity, false);

		assertTrue(entity.getCode().equals(dto.getCode()));
		assertTrue(entity.getFamily().equals(dto.getFamily()));
		assertTrue(entity.getName().equals("english"));
		assertTrue(entity.getNativeName().equals("english"));
		//assertTrue(entity.getUpdatedBy() != null);
		assertTrue(entity.getUpdDtimes() != null);
	}

}
