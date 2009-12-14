package com.gu.management.spring;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractUrlHandlerMapping;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Properties;

import static com.gu.testsupport.matchers.Matchers.collectionContainingOnly;
import static com.gu.testsupport.matchers.Matchers.collectionOfSize;
import static org.hamcrest.MatcherAssert.assertThat;

public class ManagementUrlDiscoveryServiceTest {

    @Before
	public void init() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldReturnListOfManagementUrlsGatheredFromHandlerMapItWasInitialisedWith() {
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setUrlMap(ImmutableMap.of("/management/url1", (Object) new MultiActionController()));
        mapping.initApplicationContext();

		ManagementUrlDiscoveryService service = new ManagementUrlDiscoveryService();
		service.setHandlerMappings(Arrays.<AbstractUrlHandlerMapping>asList(mapping));

        assertThat(service.getManagementUrls(), collectionContainingOnly("/management/url1"));
	}

	@Test
    @SuppressWarnings({"UnusedDeclaration"})
	public void shouldReturnPublicAndProtectedMethodsFromMultiActionController() {
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setUrlMap(ImmutableMap.of("/management/url1/**", new MultiActionController() {
            public ModelAndView publicmethod(HttpServletRequest request, HttpServletResponse response) {
				return null;
			}

            public ModelAndView protectedmethod(HttpServletRequest request, HttpServletResponse response) {
                return null;
            }

		}));
        mapping.initApplicationContext();

		ManagementUrlDiscoveryService service = new ManagementUrlDiscoveryService();
		service.setHandlerMappings(Arrays.<AbstractUrlHandlerMapping>asList(mapping));

        assertThat(service.getManagementUrls(),
                collectionContainingOnly("/management/url1/publicmethod", "/management/url1/protectedmethod"));

	}


	@Test
	public void shouldReturnUrlsFromFormControllers() throws Exception {
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();

		mapping.setUrlMap(ImmutableMap.of("/management/url1/**", new SimpleFormController() {
			@SuppressWarnings("unused")
			public ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response) {
				return null;
			}
		}));

        mapping.initApplicationContext();

		ManagementUrlDiscoveryService service = new ManagementUrlDiscoveryService();
        service.setHandlerMappings(Arrays.<AbstractUrlHandlerMapping>asList(mapping));

        assertThat(service.getManagementUrls(), collectionContainingOnly("/management/url1"));
	}


	@Test
	public void shouldReturnUrlsThatAreCustomMappedFromMultiActionController() {
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();

		PropertiesMethodNameResolver resolver = new PropertiesMethodNameResolver();

		Properties mappings = new Properties();
		mappings.put("/management/cache/clear", "methodName");
		resolver.setMappings(mappings);

		MultiActionController controller = new MultiActionController();
		controller.setMethodNameResolver(resolver);
		mapping.setUrlMap(ImmutableMap.of("/management/url1/**", controller));
        mapping.initApplicationContext();

		ManagementUrlDiscoveryService service = new ManagementUrlDiscoveryService();
        service.setHandlerMappings(Arrays.<AbstractUrlHandlerMapping>asList(mapping));

        assertThat(service.getManagementUrls(), collectionContainingOnly("/management/cache/clear"));

	}

    @Test
    public void shouldJustPickUpThePathSpecifiedFromAnnotatedControllers() {
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();

		mapping.setUrlMap(ImmutableMap.of("/management/url1/**", new Object() {
			@RequestMapping("/mangement/url1/something")
			public void makeItSo() {
			}
		}));

        mapping.initApplicationContext();

        ManagementUrlDiscoveryService service = new ManagementUrlDiscoveryService();
        service.setHandlerMappings(Arrays.<AbstractUrlHandlerMapping>asList(mapping));

        assertThat(service.getManagementUrls(), collectionContainingOnly("/mangement/url1/something"));

    }

    @Test
    public void shouldNeverIncludeDuplicates() {
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setUrlMap(ImmutableMap.of(
                "/management/url1", new MultiActionController()
        ));
        mapping.initApplicationContext();

        ManagementUrlDiscoveryService service = new ManagementUrlDiscoveryService();

        // NOTE: include mapping twice in this list this time
        service.setHandlerMappings(Arrays.<AbstractUrlHandlerMapping>asList(mapping, mapping));

        assertThat(service.getManagementUrls(), collectionOfSize(1));

    }


}

