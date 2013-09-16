Name:           ___NAMEPREFIX___aurora
Version:        1.1
Release:        ___BUILDNUMBER___%{?dist}
Summary:        Aurora
License:	    Licence
BuildArch:      noarch

# default prefix
Prefix:		/opt

#BuildRequires:  gcc libxml2-devel libxslt-devel openssl-devel python-virtualenv libyaml-devel mysql-devel
#Requires:       java-1.6.0-openjdk
Obsoletes:	aurora

%description
Aurora

# build step
%build

# this is list of files which package provides
# %{prefix} - will replaced to user specified --prefix
%files
%config(noreplace) %{_sysconfdir}/aurora/aurora.sh
%config(noreplace) %{_sysconfdir}/aurora/Config.json
%config %{_sysconfdir}/init.d/*




# on this step, copy builded virtualenv and etc to buildroot dir
%install
mkdir -p %{buildroot}/etc/aurora/
mkdir -p %{buildroot}/etc/init.d

mkdir -p %{buildroot}%{prefix}
mkdir -p %{buildroot}%{prefix}/aurora/target


cp -r %{_sourcedir}/target/standalone* %{buildroot}%{prefix}/aurora/target/
cp -r %{_sourcedir}/tools/aurora.init %{buildroot}/etc/init.d/

chmod +x %{buildroot}/etc/init.d/aurora.init

#cp -f %{_sourcedir}/tools/Config.json   %{buildroot}/root/

cp -f %{_sourcedir}/tools/Config.json   %{buildroot}/etc/aurora/
cp -r %{_sourcedir}/tools/aurora.sh     %{buildroot}/etc/aurora/

# clean step, remove builded virtualenv andt etc
%clean
rm -rf %{buildroot}
rm -rf %{_builddir}

%files
%config %{_sysconfdir}/init.d/*
%config %{_sysconfdir}/aurora/*
%{prefix}/aurora



%pre

#if [ `grep -c ^aurora /etc/passwd` = "0" ]; then
#/usr/sbin/useradd -c 'aurora User' -d /etc/aurora -p '09f8c3e75e563dd37aa603524ba5e70c' -s /bin/bash aurora
#fi

# after rpm installed step
%post
chown aurora:root /etc/aurora -R
chown aurora:root /opt/aurora -R


%preun
if [ "$1" == "0" ]; then
# this is uninstall, not upgrade
# stopping services
    /etc/init.d/aurora.init stop || :
fi
