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
package org.demoiselle.jsf.template;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.faces.model.DataModel;

/**
 *
 * <p>
 * Extends the {@link PageBean} interface to provide a page controller with methods
 * to list a collection of instances of an entity bean and mark subsets of this collection
 * to batch tasks like removing or refreshing multiple instances in a single request.
 * </p>
 *
 * @param <T> Type of the entity bean.
 * @param <I> Type of the unique identifier of the entity .
 *
 * @author SERPRO
 * 
 */
public interface ListPageBean<T, I> extends PageBean {

	/**
	 * @return The collection this bean is intended to manage
	 * as a {@link DataModel}.
	 *
	 */
	DataModel<T> getDataModel();

	/**
	 * @return The collection this bean is intended to manage
	 * as a {@link Collection}.
	 *
	 */
	Collection<T> getResultList();

	/**
	 * Action intended to trigger the listing of entities on the page
	 */
	void list();

	Map<I, Boolean> getSelection();

	void setSelection(Map<I, Boolean> selection);
}
