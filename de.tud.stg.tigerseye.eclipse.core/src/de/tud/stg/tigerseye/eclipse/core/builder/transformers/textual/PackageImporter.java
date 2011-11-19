package de.tud.stg.tigerseye.eclipse.core.builder.transformers.textual;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.tud.stg.tigerseye.dslsupport.DSLInvoker;
import de.tud.stg.tigerseye.eclipse.core.api.TransformationType;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.Context;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.FileType;
import de.tud.stg.tigerseye.eclipse.core.builder.transformers.TextualTransformation;

public class PackageImporter implements TextualTransformation {


	@Override
	public StringBuffer transform(Context context, StringBuffer sb) {
		sb = this.importPackages(context, sb);
		return sb;
	}

	private final static Pattern packageDeclaration = Pattern.compile(
			"(.*)(package (?:.*?);)(.*?)(?:@EDSL\\(.*?\\))?(.*)", Pattern.DOTALL);

	private StringBuffer importPackages(Context context, StringBuffer input) {

		Matcher matcher = packageDeclaration.matcher(input);

		StringBuffer out = new StringBuffer();
		StringBuilder sb = new StringBuilder();

		LinkedList<String> imports = new LinkedList<String>();

	for (Class<?> clazz : context.getDSLClasses()) {
			imports.add(clazz.getCanonicalName());
		}
	// working to unify under DSLInvoker
	imports.add(DSLInvoker.class.getCanonicalName());

	if (matcher.find() && !(context.getDSLClasses().length < 1)) {
			sb.append("$1");
			sb.append("$2\n");
			sb.append('\n');

	    // imports.add("de.tud.stg.popart.builder.utils.DSLInvoker");
	    imports.add(DSLInvoker.class.getCanonicalName());
			sb.append("$3");
			sb.append("$4");

			matcher.appendReplacement(out, sb.toString());
	} else if (!(context.getDSLClasses().length < 1)) {
			out.append(sb);
		}

		matcher.appendTail(out);

		this.addImports(imports, out);
		return out;
	}

	private static Pattern packagePosition = Pattern.compile("package [A-Za-z0-9\\.]+?;?\\s+");

    // XXX(Leo_Roos;Nov 18, 2011) only static until BootStrapTransformation no
    // longer necessary
    public static void addImports(LinkedList<String> imports, StringBuffer out) {
		Matcher matcher = packagePosition.matcher(out);

		int position = 0;

		if (matcher.find()) {
			position = matcher.end();
		}

		StringBuilder sb = new StringBuilder();

		for (String im : imports) {
			sb.append("import ").append(im).append(';');
			sb.append('\n');
		}

		out.insert(position, sb);
	}

	@Override
	public String toString() {
		return "package importer: " + super.toString();
	}

	@Override
	public Set<String> getAssurances() {
		return Collections.emptySet();
	}

	@Override
	public Set<String> getRequirements() {
		return Collections.emptySet();
	}

	@Override
	public Set<TransformationType> getSupportedFileTypes() {
		return TextualTransformationUtils.getSetForFiletypes(FileType.TIGERSEYE,
				FileType.JAVA, FileType.GROOVY);
	}

	@Override
	public String getDescription() {
		return "Adds necessary import statements specific for each DSL.";
	}

}
