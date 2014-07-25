package spring.javaconfig.sample;

import javax.servlet.Filter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import spring.javaconfig.sample.config.WebSecurityConfig;
import spring.javaconfig.sample.config.WebMvcConfig;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;
import static org.hamcrest.CoreMatchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {WebSecurityConfig.class, WebMvcConfig.class})
@WebAppConfiguration
public class WebSecurityTest {

	@Autowired
	private Filter springSecurityFilterChain;

	@Autowired
	private WebApplicationContext context;

	private MockMvc mvc;

	@Before
	public void setup() {
		mvc = MockMvcBuilders
				.webAppContextSetup(context)
				.addFilters(springSecurityFilterChain)
				.build();

	}

	@Test
	public void htmlRequestWithAnonymous() throws Exception {
		mvc.perform(get("/member")
				.accept(MediaType.TEXT_HTML)
		)
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("http://localhost/login"));
	}

	@Test
	public void ajaxRequestWithAnonymous() throws Exception {
		mvc.perform(
				get("/member")
				.header("X-Requested-With", "XMLHttpRequest")
		)
				.andExpect(status().isUnauthorized());
	}

	@Test
	public void requestProtectedPageWithUser() throws Exception {
		mvc.perform(
				get("/member")
				.accept(MediaType.TEXT_HTML)
				.with(user("user").roles("USER"))
		)
				.andExpect(status().is2xxSuccessful());
	}

	@Test
	public void ajaxRequestWithUser() throws Exception {
		mvc.perform(
				get("/test")
				.header("X-Requested-With", "XMLHttpRequest")
				.with(user("user").roles("USER"))
		)
				.andExpect(status().is2xxSuccessful())
				.andExpect(jsonPath("$.message", is("ok")));
	}

	@Test
	public void requestMemberPageWithUser() throws Exception {
		mvc.perform(
				get("/member")
				.accept(MediaType.TEXT_HTML)
				.with(user("user").roles("USER"))
		)
				.andExpect(status().is2xxSuccessful())
				.andExpect(xpath("//div[@id='admin_section']").doesNotExist());
	}

	@Test
	public void requestMemberPageWithAdmin() throws Exception {
		mvc.perform(
				get("/member")
				.accept(MediaType.TEXT_HTML)
				.with(user("admin").roles("ADMIN"))
		)
				.andExpect(status().is2xxSuccessful())
				.andExpect(xpath("//div[@id='admin_section']").exists());
	}
}
