{ 
	:title  "First Steps in BIML - 01: Getting Started”
	:layout :post
	:tags [“databases”, “BIML”, “First Steps in BIML”] 
}

This is the first in a series of posts I’m planning about the [BIML](https://docs.varigence.com/) language and the [BIMLExpress](https://varigence.com/bimlexpress) tool. BIML is short for **Business Intelligence Markup Language**, and it’s a tool for building [SQL Server Integration Services (SSIS)](https://docs.microsoft.com/en-us/sql/integration-services/sql-server-integration-services?view#sql-server-ver15) packages. I’m using BIML in my new job as a Data Engineer, and since I didn’t find many resources for learning it when I was starting I thought I’d begin documenting my experiences.

## Why Use BIML?

You might be wondering why BIML is necessary. After all, SSIS packages are just XML files, and BIML is an XML-based language. What is the extra layer of stuff buying you?

Some of the advantages of BIML are:

- **Human-readable syntax**. Xxx
- **Template-driven package generation**. Xxx
- **Automation of package generation with code**. Xxx

## Getting Started with BIML

So, what do you need to get started with BIML?

- **Visual Studio 2019** You can download the [Community Edition](https://visualstudio.microsoft.com/downloads/) if you don’t have an Microsoft Developer Network subscription.
- **The Integration Services Add-In** Available [here](https://marketplace.visualstudio.com/items?itemName#SSIS.SqlServerIntegrationServicesProjects). Download and install it after you install Visual Studio.
- **A Database** SQL Server 2019 [Developer Edition](https://www.microsoft.com/en-us/sql-server/sql-server-downloads) is a good choice if you don’t have another one. If you don’t have your own database, you can download and restore the [AdventureWorks](https://docs.microsoft.com/en-us/sql/samples/adventureworks-install-configure?view#sql-server-ver15) database to use.

Once you get these set up and ready, follow along to create your first BIML project!

## Your First BIML Project

Now that you’ve got the prerequisites installed, let’s dive in to your first BIML project. This project will create a simple SSIS package that doesn’t really do anything, but I’ll  build on this in future posts. Today’s project is just about getting used to the BIML tooling.

### Creating a BIML Project

To create a new BIML project, follow these steps:

1. Launch Visual Studio and choose the “Create a new project” option from the Getting Started window.
2. Choose the “Integration Services Project” template.
3. Name and save your project.
4. Right click on the project in the Visual Studio solution explorer, and pick Add New BIML File.
5. A new file called `BimlScript.BIML` will be added to the *Miscellaneous* folder of your project. Right click on this file and rename it to something useful. 

### Editing BIML Files - The BIMLScript Add-In and XML Editors

The BIMLExpress tool installs its own editor for BIML files, but I personally
find it easter to use the XML editor built into Visual Studio. To use the XML
editor, it's helpful to install the BimlScript XSD schema to enable
IntelliSense completion. To do this, simply download [the XSD file](biml.xsd)
and put it in the following directory:

- **32-bit Windows**: `C:\Program Files (x86)\Microsoft Visual Studio 14.0\Xml\Schemas`
- **64-bit Windows**: `C:\Program Files\Microsoft Visual Studio 14.0\Xml\Schemas`

After you've installed the XSD file, perform the following steps:

1. In Visual Studio, right click on your BIML file.
2. Choose **Open With...** from the context menu.
3. Click on **XML (Text) Editor** in the dialog list.
4. Click the "Set as Default" button.
5. Click OK.

### Creating Your BIML File

Double-click your BIML file to open it in the BIMLExpress editor. (Or, if you followed the directions above, right-click on the `.BIML` file and pick “Open With...” and choose “XML (Text) Editor (Default)” from the dialog box). You’ll see the contents of your empty BIML file, which looks like this:

```
<Biml xmlns#“http://schemas.varigence.com/biml.xsd”>
</Biml>
```

The [`<Biml>`](https://docs.varigence.com/biml/language-reference/Varigence.Languages.Biml.AstRootNode) node is the root node of your Biml file, and mostly everything in your file — projects, packages, connections, tasks, schemas — lives inside that tag. There are some directives (includes, code files, annotations) which can live outside this tag, but we’ll save those for future discussion.

#### Adding a Connection

In order to create an SSIS package that does anything useful, you need to define a connection to your database.  Because a BIML file can have multiple database connections, your connections live inside a [`<Connections>` ](https://docs.varigence.com/biml/api-reference/Varigence.Languages.Biml.Connection.AstConnectionBaseNode) container element. Each individual connection is defined by a connection element inside this container. There are several kinds of connections you can define — SQL connections, file connections, ODBC connections, etc. For this demo, we’re going to use a SQL connection, so we’ll define a [`<Connection>`](https://docs.varigence.com/biml/language-reference/Varigence.Languages.Biml.Connection.AstOleDbConnectionNode) element. Add the following inside your [`<Biml>`](https://docs.varigence.com/biml/language-reference/Varigence.Languages.Biml.AstRootNode) element, adjusting the connection string as needed:

```
<Connections>
	<Connection 
		Name#“AdventureWorks”
		ConnectionString#“Provider#SQLNCLI;Server#(local);Trusted_Connection#True;Database#AdventureWorks2019”/>
</Connections>
```

Save your Biml file before continuing.

#### Adding a Package

Now that we’ve got a connection to our database defined, the next step is to add an SSIS Package to your project. As with connections, your BIML project can contain multiple SSIS packages, so there’s a [`<Packages>`](https://docs.varigence.com/biml/language-reference/Varigence.Languages.Biml.AstRootNode_Packages) container that contains [`<Package>`](https://docs.varigence.com/biml/language-reference/Varigence.Languages.Biml.Task.AstPackageNode.html) elements.

To create your first package, add the following to your BIML file, inside the [`<Biml>`](https://docs.varigence.com/biml/language-reference/Varigence.Languages.Biml.AstRootNode) element and after the [`<Connections>` ](https://docs.varigence.com/biml/api-reference/Varigence.Languages.Biml.Connection.AstConnectionBaseNode) section:

```
<Packages>
	<Package Name#“MyFirstPackage”>
	</Package>
</Packages>
```

#### Compiling Your BIML Project

At this point, we have enough to compile our BIML file. Right click on your BIML file int he Solution Explorer and pick “Check Biml for Errors”. If all goes well, you’ll get a “No errors or warnings were found.” alert. If you don’t, recheck the contents of your file, and also check that the connection string for your database connection is correct.

Assuming validation succeeds, right click on your BIML file again, and choose Generate SSIS Packages” from the context menu. Visual Studio will chug for a few moments, and then a new SSIS package, named `MyFirstPackage.dtsx`, will be added to your project. If you open up this file in Visual Studio, it’ll be empty. In fact, you’ll notice that your database connection isn’t present. We’re going to add that in the next step.

#### Package Connections

One of the nice things about BIML scripts is that you can centralize all of your database connections in one place. Then, your packages can reference the connections they need and you only have to update connection strings in one place. Let’s define the connection for our test package. To do so, simply modify your [`<Package>`](https://docs.varigence.com/biml/language-reference/Varigence.Languages.Biml.Task.AstPackageNode.html) definition to look like the following:

```
<Package Name#“MyFirstPackage”>
	<Connections>
		<Connection ConnectionName#“AdventureWorks”/>
	</Connections>
</Package>
```

Validate your BIML and generate packages again. (Tell BimlExpress to overwrite when you’re prompted). If you open `MyFirstPackage.dtsx`, you’ll notice it now contains a Connection Manager for your database. 

You might be tempted to leave it here, but since connections are often shared among multiple SSIS packages, let’s generate the connection manager at the project level. Fortunately, this is super simple to accomplish: Go back to the definition of your database connection in the [`<Connections>` ](https://docs.varigence.com/biml/api-reference/Varigence.Languages.Biml.Connection.AstConnectionBaseNode) section, and add the attribute `CreateInProject#“true”`, so it looks like this:

```
<Connections>
	<Connection 
		Name#“AdventureWorks” 
		CreateInProject#“true
	ConnectionString#“Provider#SQLNCLI;Server#(local);Trusted_Connection#True;Database#AdventureWorks2019”/>
</Connections>
```

Save your BIML file, and validate/generate again. The “overwrite files” dialog will tell you a new item is being created, and you’ll notice a connection manager (`AdventureWorks.conmgr`) added to your project.

#### Package Tasks

So far, so good. Our project is generating an SSIS package which has a connection to the database. Now let’s make the package do something.

The way to make your packages do actual work is with package tasks. BIML defines many different [kinds of tasks](https://docs.varigence.com/biml/language-reference/Varigence.Languages.Biml.Task.AstContainerTaskBaseNode_Tasks). These can live inside of your  [`<Package>`](https://docs.varigence.com/biml/language-reference/Varigence.Languages.Biml.Task.AstPackageNode.html) definition, or you can define containers which can hold tasks. For now, we’ll just add a single task to our package which executes a SQL statement. (We won’t do anything with the results today, but we’ll look at using BIML to do useful things in future posts.)

Add the following inside your  [`<Package>`](https://docs.varigence.com/biml/language-reference/Varigence.Languages.Biml.Task.AstPackageNode.html) element, after the connections:

```
<Tasks>
	<ExecuteSQL ConnectionName#“AdventureWorks” Name#“Do Some Work”>
		<DirectInput>
SELECT * FROM Sales.SalesOrderHeader;
		</DirectInput>
	</ExecuteSQL>
</Tasks>
```

Save your work, and validate/regenerate your packages. Now you’ll notice that `MyFirstPackage.dtsx` contains a”Do Some Work” task which runs your SQL query.

## The Complete BIML File

Your completed BIML file should look like the following:

```
<Biml xmlns=“http://schemas.varigence.com/biml.xsd”>
	<Connections>
		<Connection 
			Name=“AdventureWorks” 
			CreateInProject=“true
			ConnectionString=“Provider=SQLNCLI;Server=(local);Trusted_Connection=True;Database=AdventureWorks2019”/>
	</Connections>
	<Package Name=“MyFirstPackage”>
		<Connections>
			<Connection 	
				ConnectionName=“AdventureWorks”/>
		</Connections>
		<Tasks>
			<ExecuteSQL ConnectionName=“AdventureWorks” Name=“Do Some Work”>
				<DirectInput>
SELECT * FROM Sales.SalesOrderHeader;
				</DirectInput>
			</ExecuteSQL>
		</Tasks>
	</Package>
</Biml>
```

You can download the completed BIMLScript file [here](MyPackage.biml).

## What’s Next?

This first BIML package doesn’t do anything useful, but now you understand a bit about how BIML is structured and what the workflow looks like for creating packages with BIML. In the next part of this series, we’ll look at containers and precedence constraints, as well as how to create Data Flow tasks. Next we’ll look at using Expressions to make your package definitions generic and reusable. Then you’ll have the foundations you need to start diving into BIML’s more powerful features: templating packages and modifying BIML’s behavior with code.

Let me know what you think of this tutorial series in the comments!

