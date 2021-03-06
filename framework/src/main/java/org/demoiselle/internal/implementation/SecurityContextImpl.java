/*
 * Demoiselle Framework
 * Copyright (C) 2010 SERPRO
 * ----------------------------------------------------------------------------
 * This file is part of Demoiselle Framework.
 * 
 * Demoiselle Framework is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License version 3
 * along with this program; if not,  see <http://www.gnu.org/licenses/>
 * or write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA  02110-1301, USA.
 * ----------------------------------------------------------------------------
 * Este arquivo é parte do Framework Demoiselle.
 * 
 * O Framework Demoiselle é um software livre; você pode redistribuí-lo e/ou
 * modificá-lo dentro dos termos da GNU LGPL versão 3 como publicada pela Fundação
 * do Software Livre (FSF).
 * 
 * Este programa é distribuído na esperança que possa ser útil, mas SEM NENHUMA
 * GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer MERCADO ou
 * APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral GNU/LGPL em português
 * para maiores detalhes.
 * 
 * Você deve ter recebido uma cópia da GNU LGPL versão 3, sob o título
 * "LICENCA.txt", junto com esse programa. Se não, acesse <http://www.gnu.org/licenses/>
 * ou escreva para a Fundação do Software Livre (FSF) Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02111-1301, USA.
 */
package org.demoiselle.internal.implementation;


import org.demoiselle.annotation.literal.NameQualifier;
import org.demoiselle.annotation.literal.StrategyQualifier;
import org.demoiselle.exception.DemoiselleException;
import org.demoiselle.internal.configuration.SecurityConfig;
import org.demoiselle.security.*;
import org.demoiselle.util.ResourceBundle;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Named;
import java.io.Serializable;
import java.security.Principal;

/**
 * <p>
 * This is the default implementation of {@link SecurityContext} interface.
 * </p>
 *
 * @author SERPRO
 */
@Dependent
@Named("securityContext")
public class SecurityContextImpl implements SecurityContext {

	private static final long serialVersionUID = 1L;

	private transient ResourceBundle bundle;

	private Authenticator authenticator;

	private Authorizer authorizer;

	private Authenticator getAuthenticator() {
		if (this.authenticator == null) {
			Class<? extends Authenticator> type = getConfig().getAuthenticatorClass();

			if (type != null) {
				this.authenticator = CDI.current().select(type).get(); //Beans.getReference(type);
			} else {
				this.authenticator = CDI.current().select(Authenticator.class, new StrategyQualifier()).get(); // Beans.getReference(Authenticator.class, new StrategyQualifier());
			}
		}

		return this.authenticator;
	}

	private Authorizer getAuthorizer() {
		if (this.authorizer == null) {
			Class<? extends Authorizer> type = getConfig().getAuthorizerClass();

			if (type != null) {
				this.authorizer = CDI.current().select(type).get(); //Beans.getReference(type);
			} else {
				this.authorizer = CDI.current().select(Authorizer.class, new StrategyQualifier()).get(); //Beans.getReference(Authorizer.class, new StrategyQualifier());
			}
		}

		return this.authorizer;
	}

	/**
	 * @see org.demoiselle.security.SecurityContext#hasPermission(String, String)
	 */
	@Override
	public boolean hasPermission(String resource, String operation) {
		boolean result = true;

		if (getConfig().isEnabled()) {
			checkLoggedIn();

			try {
				result = getAuthorizer().hasPermission(resource, operation);

			} catch (DemoiselleException cause) {
				throw cause;

			} catch (Exception cause) {
				throw new AuthorizationException(cause);
			}
		}

		return result;
	}

	/**
	 * @see org.demoiselle.security.SecurityContext#hasRole(String)
	 */
	@Override
	public boolean hasRole(String role) {
		boolean result = true;

		if (getConfig().isEnabled()) {
			checkLoggedIn();

			try {
				result = getAuthorizer().hasRole(role);

			} catch (DemoiselleException cause) {
				throw cause;

			} catch (Exception cause) {
				throw new AuthorizationException(cause);
			}
		}

		return result;
	}

	/**
	 * @see org.demoiselle.security.SecurityContext#isLoggedIn()
	 */
	@Override
	public boolean isLoggedIn() {
		boolean result = true;

		if (getConfig().isEnabled()) {
			result = getUser() != null;
		}

		return result;
	}

	/**
	 * @see org.demoiselle.security.SecurityContext#login()
	 */
	@Override
	public void login() {
		if (getConfig().isEnabled()) {

			try {
				getAuthenticator().authenticate();

			} catch (DemoiselleException cause) {
				throw cause;

			} catch (Exception cause) {
				throw new AuthenticationException(cause);
			}

			CDI.current().getBeanManager().fireEvent(new AfterLoginSuccessful() {

				private static final long serialVersionUID = 1L;
			});
//			Beans.getBeanManager().fireEvent(new AfterLoginSuccessful() {
//
//				private static final long serialVersionUID = 1L;
//			});
		}
	}

	/**
	 * @see org.demoiselle.security.SecurityContext#logout()
	 */
	@Override
	public void logout() throws NotLoggedInException {
		if (getConfig().isEnabled()) {
			checkLoggedIn();

			try {
				getAuthenticator().unauthenticate();

			} catch (DemoiselleException cause) {
				throw cause;

			} catch (Exception cause) {
				throw new AuthenticationException(cause);
			}

			CDI.current().getBeanManager().fireEvent(new AfterLogoutSuccessful() {

				private static final long serialVersionUID = 1L;
			});
//			Beans.getBeanManager().fireEvent(new AfterLogoutSuccessful() {
//
//				private static final long serialVersionUID = 1L;
//			});
		}
	}

	/**
	 * @see org.demoiselle.security.SecurityContext#getUser()
	 */
	@Override
	public Principal getUser() {
		Principal user = getAuthenticator().getUser();

		if (!getConfig().isEnabled() && user == null) {
			user = new EmptyUser();
		}

		return user;
	}

	private SecurityConfig getConfig() {
		return CDI.current().select(SecurityConfig.class).get();
//		return Beans.getReference(SecurityConfig.class);
	}

	public void checkLoggedIn() throws NotLoggedInException {
		if (!isLoggedIn()) {
			throw new NotLoggedInException(getBundle().getString("user-not-authenticated"));
		}
	}

	private ResourceBundle getBundle() {
		if (bundle == null) {
			bundle = CDI.current().select(ResourceBundle.class, new NameQualifier("demoiselle-core-bundle")).get();
//			bundle = Beans.getReference(ResourceBundle.class, new NameQualifier("demoiselle-core-bundle"));
		}

		return bundle;
	}

	private static class EmptyUser implements Principal, Serializable {

		private static final long serialVersionUID = 1L;

		@Override
		public String getName() {
			return "demoiselle";
		}
	}
}
