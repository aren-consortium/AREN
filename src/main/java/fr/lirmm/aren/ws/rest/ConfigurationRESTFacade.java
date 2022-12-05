package fr.lirmm.aren.ws.rest;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import fr.lirmm.aren.model.Configuration;
import fr.lirmm.aren.model.User.Authority;
import fr.lirmm.aren.service.ConfigurationService;

/**
 * JAX-RS resource class for Categories managment
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@RequestScoped
@Path("configurations")
public class ConfigurationRESTFacade extends AbstractRESTFacade<Configuration> {

  @Inject
  private ConfigurationService configService;

  @Inject
  private Provider<Properties> properties;

  /**
   *
   * @return
   */
  @Override
  protected ConfigurationService getService() {
    return configService;
  }

  /**
   * 
   * @param configurations
   * @return
   */
  @PUT
  @RolesAllowed({ "SUPERADMIN" })
  public Set<Configuration> editAll(Set<Configuration> configurations) {
    getService().updateAll(configurations);
    return this.findAll();
  }

  /**
   * Fetch all the entity of the givent type
   *
   * @return a set of entity
   */
  @Override
  @RolesAllowed({ "GUEST" })
  public Set<Configuration> findAll() {
    Set<Configuration> configs = new HashSet<>();
    properties.get().forEach((key, value) -> {
      if (getUser().is(Authority.SUPERADMIN) || key.toString().startsWith("rules.")) {
        Configuration aConf = new Configuration(key.toString(), value.toString());
        configs.add(aConf);
      }
    });
    return configs;
  }
}
