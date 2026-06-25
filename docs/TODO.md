# TODO

## 构建和CI

增加todo的md

普通build只对外暴露build，build latest，build list，build latest list。push是否需要？

风味build只对外暴露方法，build，build latest，build list，build latest list，push ，push latest，push list，push latest list

list相关方法额外增加all gradle任务

增加一个组合入口方法，自动生成所有build和push，增加多平台镜像发布任务

是否可以并发加速，test相关任务可以交给另一个job？build子任务能否并发执行？所有docker相关任务命名直观一些。docker镜像增加描述。docker增加jre支持的所有平台，考虑natvie会不会报错？

release也按风味组装，提供gradle方法统一构建(增加参数是否同步移动到指定目录)，多平台多风味客户端可执行文件+前端压缩包+便携版+安装版+jar包

增加只获取环境变量的方法？(不支持其他方式传参？)

提取判断是否用镜像源方法,CI 环境变量改为是否用镜像源，同时也执行CI环境变量？

github action 增加各类缓存

## 首页

docs编译为html

增加home首页简单html,css。 demo网页放在/demo，docs放在docs。首页组装产物，超链接到文档和演示

支持可选添加备案信息
