Name:           ___NAMEPREFIX___asgard
Version:        1.1
Release:        ___BUILDNUMBER___%{?dist}
Summary:        Asgard
License:	    Licence
BuildArch:      noarch

# default prefix
Prefix:		/opt

#BuildRequires:  gcc libxml2-devel libxslt-devel openssl-devel python-virtualenv libyaml-devel mysql-devel
#Requires:       java-1.6.0-openjdk
Obsoletes:	asgard

%description
Asgard

# build step
%build

# this is list of files which package provides
# %{prefix} - will replaced to user specified --prefix
%files
%config(noreplace) %{_sysconfdir}/asgard/asgard.sh
%config(noreplace) %{_sysconfdir}/asgard/Config.json
%config %{_sysconfdir}/init.d/*




# on this step, copy builded virtualenv and etc to buildroot dir
%install
mkdir -p %{buildroot}/etc/asgard/
mkdir -p %{buildroot}/etc/init.d

mkdir -p %{buildroot}%{prefix}
mkdir -p %{buildroot}%{prefix}/asgard/target


cp -r %{_sourcedir}/target/standalone* %{buildroot}%{prefix}/asgard/target/
cp -r %{_sourcedir}/tools/asgard.init %{buildroot}/etc/init.d/

chmod +x %{buildroot}/etc/init.d/asgard.init

#cp -f %{_sourcedir}/tools/Config.json   %{buildroot}/root/

cp -f %{_sourcedir}/tools/Config.json   %{buildroot}/etc/asgard/
cp -r %{_sourcedir}/tools/asgard.sh     %{buildroot}/etc/asgard/

# clean step, remove builded virtualenv andt etc
%clean
rm -rf %{buildroot}
rm -rf %{_builddir}

%files
%config %{_sysconfdir}/init.d/*
%config %{_sysconfdir}/asgard/*
%{prefix}/asgard



%pre

#if [ `grep -c ^asgard /etc/passwd` = "0" ]; then
#/usr/sbin/useradd -c 'asgard User' -d /etc/asgard -p '09f8c3e75e563dd37aa603524ba5e70c' -s /bin/bash asgard
#fi

# after rpm installed step
%post
chown asgard:root /etc/asgard -R
chown asgard:root /opt/asgard -R


%preun
if [ "$1" == "0" ]; then
# this is uninstall, not upgrade
# stopping services
    /etc/init.d/asgard.init stop || :
fi
