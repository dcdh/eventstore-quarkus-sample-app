## Prerequists

Have nodejs install on your computer

### nodejs installation on centos7
> https://tecadmin.net/install-latest-nodejs-and-npm-on-centos/

```
yum install -y gcc-c++ make
curl -sL https://rpm.nodesource.com/setup_12.x | sudo -E bash -
sudo yum install nodejs -y

npm -v
6.14.4

node -v
v12.16.3
```

### install angular cli globally on the computer

sudo npm install @angular/cli -g

ng --version

### from `todo-public-frontend` create frontend app

ng new frontend

