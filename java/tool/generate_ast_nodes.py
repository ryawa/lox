#!/usr/bin/env python
import sys


from pathlib import Path


def define_grammar(output_dir, base_name, types):
    output_dir.mkdir(exist_ok=True)
    path = output_dir / f"{base_name}.java"
    with open(path, "w") as f:
        f.write(
            f"package lox;\n"
            f"\n"
            f"import java.util.List;\n"
            f"\n"
            f"abstract class {base_name} {{\n"
        )

        define_ast_operator(f, base_name, types)
        f.write("\n")

        # The base apply() method
        f.write("    abstract <R> R apply(AstOperator<R> astOperator);\n")

        # AST classes
        for class_name, field_list in types.items():
            f.write("\n")
            define_type(f, base_name, class_name, field_list)

        f.write("}\n")


def define_ast_operator(f, base_name, types):
    f.write("    interface AstOperator<R> {\n")
    for type_name, _ in types.items():
        f.write(
            f"        R on{type_name}{base_name}("
            f"{type_name} {base_name.lower()}"
            f");\n"
        )
    f.write("    }\n")


def define_type(f, base_name, class_name, field_list):
    f.write(
        f"    static class {class_name} extends {base_name} {{\n"
        # Constructor
        f"        public {class_name}({field_list}) {{\n"
    )

    fields = field_list.split(", ")

    # Store parameters in fields
    for field in fields:
        name = field.split(" ")[1]
        f.write(f"            this.{name} = {name};\n")

    f.write("        }\n" "\n")

    # Visitor pattern
    f.write(
        f"        @Override\n"
        f"        <R> R apply(AstOperator<R> astOperator) {{\n"
        f"            return astOperator.on{class_name}{base_name}(this);\n"
        f"        }}\n"
        f"\n"
    )

    # Fields
    for field in fields:
        f.write(f"        final {field};\n")

    f.write("    }\n")


if __name__ == "__main__":
    if len(sys.argv) != 2:
        print(f"Usage: {sys.argv[0]} <output directory>")
        sys.exit(64)
    define_grammar(
        Path(sys.argv[1]),
        "Expr",
        {
            "Binary": "Expr left, Token operator, Expr right",
            "Ternary": "Expr left, Token leftOperator, Expr middle, Token rightOperator, Expr right",
            "Grouping": "Expr expression",
            "Literal": "Object value",
            "Unary": "Token operator, Expr right",
        },
    )
