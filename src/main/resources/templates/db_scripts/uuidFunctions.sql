DO $uuidftl$
BEGIN
<#include "uuid.ftl">
EXCEPTION WHEN OTHERS THEN NULL;
END $uuidftl$;

DO $$ BEGIN DROP FUNCTION count_estimate_smart; EXCEPTION WHEN OTHERS THEN END; $$;
DO $$ BEGIN DROP FUNCTION count_estimate_smart_depricated; EXCEPTION WHEN OTHERS THEN END; $$;
DO $$ BEGIN DROP FUNCTION set_id_injson_actual_opening_hours; EXCEPTION WHEN OTHERS THEN END; $$;
DO $$ BEGIN DROP FUNCTION set_id_injson_exceptional_hours; EXCEPTION WHEN OTHERS THEN END; $$;
DO $$ BEGIN DROP FUNCTION set_id_injson_regular_hours; EXCEPTION WHEN OTHERS THEN END; $$;
