/*
 * Copyright (c) 2005, 2006, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package com.chenjw.knife.client.attach;

public class VirtualMachineDescriptor {

	private AttachProvider provider;
	private String id;
	private String displayName;

	private volatile int hash; // 0 => not computed

	public VirtualMachineDescriptor(AttachProvider provider, String id,
			String displayName) {
		if (provider == null) {
			throw new NullPointerException("provider cannot be null");
		}
		if (id == null) {
			throw new NullPointerException("identifier cannot be null");
		}
		if (displayName == null) {
			throw new NullPointerException("display name cannot be null");
		}
		this.provider = provider;
		this.id = id;
		this.displayName = displayName;
	}

	/**
	 * Creates a virtual machine descriptor from the given components.
	 * 
	 * <p>
	 * This convenience constructor works as if by invoking the three-argument
	 * constructor as follows:
	 * 
	 * <blockquote><tt>
	 * new&nbsp;{@link #VirtualMachineDescriptor(AttachProvider, String, String)
	 * VirtualMachineDescriptor}(provider, &nbsp;id, &nbsp;id);
	 * </tt></blockquote>
	 * 
	 * <p>
	 * That is, it creates a virtual machine descriptor such that the <i>display
	 * name</i> is the same as the virtual machine identifier.
	 * 
	 * @param provider
	 *            The AttachProvider to attach to the Java virtual machine.
	 * @param id
	 *            The virtual machine identifier.
	 * 
	 * @throws NullPointerException
	 *             If <tt>provider</tt> or <tt>id</tt> is <tt>null</tt>.
	 */
	public VirtualMachineDescriptor(AttachProvider provider, String id) {
		this(provider, id, id);
	}

	/**
	 * Return the <code>AttachProvider</code> that this descriptor references.
	 * 
	 * @return The <code>AttachProvider</code> that this descriptor references.
	 */
	public AttachProvider provider() {
		return provider;
	}

	/**
	 * Return the identifier component of this descriptor.
	 * 
	 * @return The identifier component of this descriptor.
	 */
	public String id() {
		return id;
	}

	/**
	 * Return the <i>display name</i> component of this descriptor.
	 * 
	 * @return The display name component of this descriptor.
	 */
	public String displayName() {
		return displayName;
	}

	/**
	 * Returns a hash-code value for this VirtualMachineDescriptor. The hash
	 * code is based upon the descriptor's components, and satifies the general
	 * contract of the {@link java.lang.Object#hashCode() Object.hashCode}
	 * method.
	 * 
	 * @return A hash-code value for this descriptor.
	 */
	public int hashCode() {
		if (hash != 0) {
			return hash;
		}
		hash = provider.hashCode() * 127 + id.hashCode();
		return hash;
	}

	/**
	 * Tests this VirtualMachineDescriptor for equality with another object.
	 * 
	 * <p>
	 * If the given object is not a VirtualMachineDescriptor then this method
	 * returns <tt>false</tt>. For two VirtualMachineDescriptors to be
	 * considered equal requires that they both reference the same provider, and
	 * their {@link #id() identifiers} are equal.
	 * </p>
	 * 
	 * <p>
	 * This method satisfies the general contract of the
	 * {@link java.lang.Object#equals(Object) Object.equals} method.
	 * </p>
	 * 
	 * @param ob
	 *            The object to which this object is to be compared
	 * 
	 * @return <tt>true</tt> if, and only if, the given object is a
	 *         VirtualMachineDescriptor that is equal to this
	 *         VirtualMachineDescriptor.
	 */
	public boolean equals(Object ob) {
		if (ob == this)
			return true;
		if (!(ob instanceof VirtualMachineDescriptor))
			return false;
		VirtualMachineDescriptor other = (VirtualMachineDescriptor) ob;
		if (other.provider() != this.provider()) {
			return false;
		}
		if (!other.id().equals(this.id())) {
			return false;
		}
		return true;
	}

	/**
	 * Returns the string representation of the
	 * <code>VirtualMachineDescriptor</code>.
	 */
	public String toString() {
		String s = provider.toString() + ": " + id;
		if (displayName != id) {
			s += " " + displayName;
		}
		return s;
	}

}
