# herostory

## 问题

### 老用户看见新用户

使用netty的信道组，所有的连接加入信道组，当有新用户进来时会自动群发给所有信道``UserEntryCmd``的消息。

```java
public static ChannelGroup group=new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

@Override
public void channelActive(ChannelHandlerContext ctx){
        try{
        super.channelActive(ctx);
        group.add(ctx.channel());
        }catch(Exception e){
        LOG.error(e.getMessage(),e);
        }
        }
```

### 新用户看见老用户

如果只是加入信道，新用户是无法看见老用户的。 原因是

老用户能看到新用户，是因为新用户在加入信道组时，会群发``UserEntryCmd``的消息，前端必须收到``UserEntryResult``消息才会画出新用户。

而老用户的``UserEntryCmd``消息在之前就发过了，所以新用户才无法感知到老用户的存在。

#### 解决

新用户在入场时除了发送``UserEntryCmd``，还会发送``WhoElseIsHereCmd``，也就是会询问服务器还有哪些用户在场，服务器返回即可。

如果用Set存放User会怎样？

### 用户移动同步

用户移动广播结果即可，需要在入场时在session中保存userId

### 用户退场

重写handleRemoved()方法，当浏览器刷新或者关闭时会自动调用。

离场没有cmd，所以连接关闭时，需要广播quit_result。注意这里从session取userId需要判空，否则当场上只有一个英雄时，重启服务器后刷新会报空指针，因为刷新是先离场再入场。

### 重构

1. 广播和用户抽取
2. cmd处理抽取

   1. cmd转换 欺骗编译器
   2. cmd 映射用map
3. decoder处理抽取
