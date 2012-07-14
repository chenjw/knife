Summary: Alibaba knife
Name: alibaba-cn-knife
Version: 1.0
Release: 2012
Group: china/knife
License: Proprietary
Prefix: .
BuildArch: noarch

%description
%prep
%build
%install
%preun
%postun

%pre
##rm -rf $RPM_INSTALL_PREFIX/target

%post
##chmod 755 $RPM_INSTALL_PREFIX/target/rpm/install.sh

%files
##%verify(md5 size mtime) /home/chenjw/my_workspace/knife/dist/knife/knife.sh
%verify(md5 size mtime) /home/chenjw/my_workspace/knife/dist/knife/lib/commons-collections-3.2.1.jar
%verify(md5 size mtime) /home/chenjw/my_workspace/knife/dist/knife/lib/commons-io-1.4.jar
%verify(md5 size mtime) /home/chenjw/my_workspace/knife/dist/knife/lib/commons-lang-2.4.jar
%verify(md5 size mtime) /home/chenjw/my_workspace/knife/dist/knife/lib/commons-logging-1.1.1.jar
%verify(md5 size mtime) /home/chenjw/my_workspace/knife/dist/knife/lib/java.j2ee-1.4.jar
%verify(md5 size mtime) /home/chenjw/my_workspace/knife/dist/knife/lib/knife-agent.jar
%verify(md5 size mtime) /home/chenjw/my_workspace/knife/dist/knife/lib/knife-client.jar
%verify(md5 size mtime) /home/chenjw/my_workspace/knife/dist/knife/lib/knife-core.jar
%verify(md5 size mtime) /home/chenjw/my_workspace/knife/dist/knife/lib/misc.javassist-3.9.0.GA-sources.jar
%verify(md5 size mtime) /home/chenjw/my_workspace/knife/dist/knife/lib/misc.javassist-3.9.0.GA.jar
%verify(md5 size mtime) /home/chenjw/my_workspace/knife/dist/knife/lib/sourceforge.spring-2.5.6-sources.jar
%verify(md5 size mtime) /home/chenjw/my_workspace/knife/dist/knife/lib/sourceforge.spring-2.5.6.jar
