# A grammar for the nearly.js parser

Main -> Class | Field | Method  | MethodParameter

Class -> ("class" | "missing-class" | "interface" | "enum" | "@interface" ) _ Type {%
	function (d) {
		var ret = d[2];
		ret["elementType"] = d[0][0];
		return ret;
	}
%}


Field -> "field" _ Type "." Identifier (_ "@" _ Type):? {%
	function(d) {
		var ret = d[2];
		ret["elementType"] = "field";
		ret["fieldName"] = d[4];

		return ret;
	}
%}

Method -> "method" _ ( TypeParameters _ ):? Type _ Type "::" ( Identifier | "<init>" ) "(" (_:? Type (_:? "," _:? Type):*):? ")"
( _ "throws" _ Type (_:? "," _:? Type ):* ):? (_ "@" _ Type):?
{%
	function(d) {
		var params = [];
		if (d[9] !== null) {
			params.push(d[9][1]);
			d[9][2].forEach(function (e) {
				params.push(e[3]);
			});
		}

		var thrown = [];
		if (d[11] !== null) {
			thrown.push(d[11][3]);
			d[11][4].forEach(function (t) {
				thrown.push(t[3]);
			});
		}

		return {
			"elementType": "method",
			"typeParameters": d[2] == null ? [] : d[2][0],
			"returnType": d[3],
			"declaringType": d[5],
			"methodName": d[7][0],
			"parameters": params,
			"thrownTypes": thrown,
			"reportingType": d[12] == null ? null : d[12][3],
		};
	}
%}

MethodParameter -> "parameter" _ ( TypeParameters _ ):? Type _ Type "::" Identifier "(" (_:? (Type | "===" Type "===") (_:? "," _:? (Type | "===" Type "===")):*):? ")" (_ "@" _ Type):? {%

	function(d) {
		var params = [];
		if (d[9][1].length === 1) {
			params.push(d[9][1][0]);
		} else {
			params.push(d[9][1][1]);
		}

		var selected = 0;

		var idx = 0;
		d[9][2].forEach(function (e) {
			var ar = e[3];
			if (ar.length === 1) {
				params.push(e[3][0]);
			} else {
				params.push(e[3][1]);
				selected = idx + 1;
			}
			idx++;
		});

		return {
			"elementType": "parameter",
			"typeParameters": d[2] == null ? [] : d[2][0],
			"returnType": d[3],
			"declaringType": d[5],
			"methodName": d[7],
			"parameters": params,
			"selectedParameterIndex": selected,
			"reportingType": d[11] == null ? null : d[11][3],
			"__": d
		};
	}
%}

Type -> ClassName TypeParameters:? "[]":* {%
	function(d) {
		return {
			"type": d[0],
			"typeParameters": d[1],
			"arrayDimension":  d[2] == null ? 0 : d[2].length,
		};
	}
%}

ClassName -> Identifier ("." Identifier):* {%
	function(d) { return d[0] + d[1].map(function(e) {return e.join("")}).join(""); }
%}

Identifier -> [<a-zA-Z$_] [a-zA-Z0-9$_>]:* {%
	function(d) { return d[0] + d[1].join(""); }
%}

TypeParameters -> "<" _:? TypeParameter (_:? "," _:? TypeParameter):* _:? ">" {%
	function (d) {
		var ret = [d[2]];

 		d[3].forEach(function (e) {
 			ret.push(e[3]);
 		});

 		return ret;
	}
%}

TypeParameter -> ( "?" | Type ) ( _ TypeParameterBound ):? {%
	function (d) {
		return {
			"type": d[0][0],
			"bound": d[1] == null ? null : d[1][1],
		};
	}
%}

TypeParameterBound -> ( "extends" | "super" ) _ Type ( _ TypeParameterBound):* {%
	function (d) {
		return {
			"kind": d[0][0],
			"boundType": d[2],
			"specialization": d[3].length > 0 ? d[3][0][1] : null,
		};
	}
%}

_ -> [\s]:+     {% function(d) {return null } %}
