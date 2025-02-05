# Hexagonal-Generator

---

Hexgonal Generator is a plugin that helps you generate a **boiler-plate class** of hexagonal architecture.

If you use this plugin, following class will be generated in **just 10 seconds.**

```
.
├── adapter
│   ├── in
│   │   └── CreateOrderController.java
│   └── out
│       └── CreateOrderAdapter.java
├── application
│   └── port
│       ├── in
│       │   └── CreateOrderUseCase.java
│       └── out
│           └── CreateOrderPort.java
└── domain
    └── service
        └── CreateOrderService.java
```

## How to use

---

It’s very simple to create classes of Hexagonal-architecture.

There’s just 2 steps for this plugin.

1. Choose your programming language.(**Java / Kotlin**)
2. Enter your common prefix of classes.
   (Ex : If you enter `createOrder`, the tree structure shown above will be generated.)

ps. It was developed for personal purposes, so customization is not possible.

I support you in developing with Hexagonal Architecture!

More info : https://plugins.jetbrains.com/plugin/26418-hexagonal-generator/
