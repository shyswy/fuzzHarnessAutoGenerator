# README

# **목차**

# 1 프로젝트 목적 및 배경

Fuzzing은 소프트웨어의 취약점을 찾기 위한 테스팅 기법으로, 랜덤 또는 의도적으로 조작된 입력값을 소프트웨어에 전달하여 예상치 못한 동작을 유발하는 것입니다.

그리고 Fuzzing harness는 Fuzzing을 수행하기 위한 프레임워크 역할을 하는 작은 프로그램입니다.

Fuzzing harness를 작성하는 것은, Fuzzing 수행에 있어 중요한 과정으로, Harness가 잘 작성되지 않으면 Fuzzing이 제대로 수행되지 않거나, 소프트웨어에 손상을 입힐 수 도 있습니다. 그리고 이러한 Fuzzing Harness를 생성하는 것은 타겟 프로그램과 Fuzzing 기술, 양쪽에 대한 높은 이해도가 필요하기에 이전부터 높은 진입 장벽을 가지고 있습니다. 그러므로 저희는 이러한 어려움을 해결하기 위해, LLM을 활용하여 Fuzzing Harness를 자동 생성하는 방안에 대해 연구했습니다.

# 2 프로젝트 최종 목표

프로젝트의 세부 목표는 크게 3가지입니다.

1. 타겟 프로그램에 따라 General하게 Fuzzing harness를 생성할 수 있는 Generate Prompt 구성
2. LLM을 통해 생성된 Fuzzing harness 코드가 올바르게 빌드되지 않을 시, Error 메시지를 기반으로 harness 코드를 수정해 줄 수 있는 Fix Prompt 구성
3. 1, 2번 과정을 자동화 할 수 있는 툴(프로그램)을 개발

첫번째 세부 목표 달성을 위해 저희는 libxml2, VLC 두가지 타겟 프로그램에 대해 제너럴하게 Harness를 생성하는 Generate Prompt 작성을 시도했습니다. 첫번째로 libxml2는 라이브러리이기 때문에 상대적으로 GPT(LLM)이 학습한 양이 많을 것으로 생각하여 해당 프로그램을 첫번째 타겟 프로그램으로 선정했습니다. 그리고 두번째로, VLC의 경우 Stand-Alone 프로그램이기에 상대적으로 LLM이 학습한 양이 적을 것으로 예상하여, 학습량이 적은 프로그램에 대해 올바르게 Harness를 생성하는지 검증을 하기 위해 선정하였습니다.

두번째 세부 목표를 달성하기 위해서 저희는 Harness가 작성하는 Build- Error를 창출하는 코드들의 패턴을 분석하여, 공통적으로 발생하는 에러 사항을 수정하도록 Fix Prompt를 작성했습니다. 가장 빈번하게 발생한 문제점들로는 deprecated된 메소드를 사용하는 것, 존재하지 않는 메소드를 사용하는 것, 그리고 올바르게 Header File을 Include해오지 못하는 것 등의 문제가 있었기에 관련 Warning문을 작성하여 이런 문제점들을 완화했습니다.

마지막으로 세번째 세부 목표 달성을 위해 저희는 GPT-API를 활용해 위 과정들을 자동화할 수 있는 프로그램을 개발했습니다. 사용자가 타겟 프로그램명, 타겟 프로그램 버전, 타겟 함수명 등 타겟 프로그램 정보를 제공하면 GPT-API를 통해 Fuzzing harness를 사용자가 제공 받아, Fuzzing을 진행할 수 있습니다.

# 3 프로젝트 내용

본격적인 프로젝트를 수행하기 전, Fuzzing에 대한 지식을 쌓기 위해 Fuzzing 101 실습을 수행했습니다. 이는 Fuzzing에 대한 기초를 배우는 데 도움이 되는 10개의 실습으로 구성되었고, 이 중 1번부터 7번 실습까지 완료하였습니다. 실습에서는 주로 AFL++이라는 Fuzzer를 사용하여 Xpdf, V8, LibTIFF 등 다양한 소프트웨어를 대상으로 취약점을 찾는 방법을 학습했습니다. 

 Fuzzing 101 실습을 통해 다양한 소프트웨어를 대상으로 Fuzzing을 수행하는 법에 대한 학습을 진행했습니다.

이후 본격적으로 “딥러닝을 이용한 SW 테스트 자동 생성”라는 주제와 관련된 지식을 학습하기 위해 Google에서 자체 Fuzzing Tool인 OSS_FUZZ의 코드 커버리지 개선을 위해 LLM을 활용한 사례에 관한 문서를 학습했습니다.

(링크: https://google.github.io/oss-fuzz/research/llms/target_generation/)

해당 보고서의 내용에서, 특정 타겟 프로그램에 대해 최대 30%가량의 코드 커버리지 개선을 확인하며 가능성을 찾았기에, 이를 기반으로 다양한 Prompt를 구성하고 결과를 확인해볼 수 있었습니다.

이후 Prompt 엔지니어링을 통해 GPT가 Compile 가능한, 올바른 Fuzzing harness를 도출할 수 있도록 Prompt에 다양한 시도를 수행했습니다. 첫번째로, Fuzzing harness 코드 작성시해 DO/ Not DO 영역을 나누어 전체적인 가이드라인을 작성하고, Warning, Include GuildeLine을 작성하여 GPT 가 실수하거나, 의도를 잘못 이해할 수 있는 부분들에 대해 강조하여 표현했습니다. 그리고 해당 타겟 프로그램에 대한 몇 가지 Fuzzing harness 샘플을 제공하여 GPT가 올바르게 저희의 의도대로 Fuzzing harness를 생성하도록 가이드 했습니다. 그 결과 다양한 타겟 프로그램, 타겟 함수에 대해 제너럴하게 컴파일 가능한 Fuzzing harness를 작성하는 생성 프롬프트를 구축할 수 있었습니다. 또한 올바르지 못한 Fuzzing harness를 생성했을 시, Fix Prompt에 Build 에러 관련 정보를 넘겨 해당 Fuzzing harness를 수정하도록 구현해, 생성된 Fuzzing harness가 Compile되지 않는 상황을 대처했습니다.

# 4. 프로젝트의 기술적 내용

Fuzzing harness 생성을 위한 2가지 프롬프트, 즉, Generate Prompt, Fix Prompt에 대해 말씀드리겠습니다.

## **Generate Prompt**

Generate Prompt는 LLM에게 처음으로 Fuzzing harness를 요청하기 위한 프롬프트입니다.

Generate Prompt를 구성하고 있는 각 부분에 대한 설명을 해드리겠습니다.

### 1. **상황 설정**

- LLM에게 역할을 부여해주고, 현재 무슨 상황인지 설명해줍니다.
- LLVMFuzzerTestOneInput이라는 함수를 통해 Fuzzing harness를 요청합니다.

```
//introduction (상황 설정)

You are a security testing engineer who wants to write a C program to execute all lines 
in a given function by defining and initialising its parameters in a suitable way before 
fuzzing the function through `LLVMFuzzerTestOneInput`.
```

### 2. **DO & NOT DO**

- Fuzzing harness 생성 시, 주의사항에 대해 LLM에게 설명해주는 부분입니다.
- 사용하면 안되는 함수, 사용하면 안되는 문법 등에 대한 정보를 제공해줍니다.
- 위 프롬프트에서 [target Program Data]에는 타겟 프로그램 이름이 들어가고, [Version]에는 해당 타겟 프로그램의 버전이 입력됩니다.

```
//DO & NOT DO

Carefully study the function signature and its parameters, then follow the example problems and solutions to answer the final problem. YOU MUST call the function to fuzz in the solution.

EXTREMELY IMPORTANT: target program is [target Program Data] and version is [Version]

Try as many variations of these inputs as possible. Do not use a random number generator such as `rand()`.

All variables used MUST be declared and initialized. Carefully make sure that the variable and argument types in your code match and compiles successfully. Add type casts to make types match.

Do not create new variables with the same names as existing variables.

WRONG:

```

int LLVMFuzzerTestOneInput(const uint8_t *data, size_t size) {

void* data = Foo();

}

```

EXTREMELY IMPORTANT: If you write code using `goto`, you MUST MUST also declare all variables BEFORE the `goto`. Never introduce new variables after the `goto`.

WRONG:

```

int LLVMFuzzerTestOneInput(const uint8_t *data, size_t size) {

int a = bar();

if (!some_function()) goto EXIT;

Foo b = target_function(data, size);

int c = another_func();

EXIT:

return 0;

}

```

CORRECT:

```

int LLVMFuzzerTestOneInput(const uint8_t *data, size_t size) {

int a = bar();

Foo b;

int c;

if (!some_function()) goto EXIT;

b = target_function(data, size);

c = another_func()

EXIT:

return 0;

}

```
```

### 3. **Warning / Include Guideline**

- Fuzzing harness 생성 시, 컴파일 되지 않는 harness 생성의 빈도를 낮추기 위해, 넣어준 경고문과 가이드라인입니다.
- 좀 더 재네럴하게 타겟 프로그램에 대한 Fuzzing harness를 받을 수 있도록 넣어주었습니다.
- Deprecated된 함수를 사용하지 말아라, 사용 가능한 헤더파일만 사용해라 등의 명령을 넣어줘서 LLM이 보다 정확한 Fuzzing harness 생성을 할 수 있도록 도와주는 부분입니다.

```
//warning

warning:

1) do not include the deprecated method

2) carefully Check if the header file that includes the newly added method is correctly included.

3) make sure to utilize FuzzedDataProvider correctly

4) make sure not to use of undeclared identifier

// header file include guildeline

Include Guidelines for [target Program Data] [Version]:

1) Include only the necessary headers for the specific functionality you are using.

2) Use forward declarations when possible instead of including the entire header.

3) Prioritize including header files from the standard library over third-party libraries.

4) Group related includes together for better organization.

5) Avoid including headers in header files whenever possible to minimize dependencies.

6) Use include guards to prevent multiple inclusion of the same header.

7) Ensure that included headers are compatible with the version of the library being used.
```

### 4. **Problem & Solution Example**

- Fuzzing harness 생성 시, LLM이 보다 효과적으로 harness를 생성하도록 하기 위해, 특정 함수에 대한 Fuzzing harness Example을 제공해주는 부분입니다.
- Example은 Google OSS-Fuzz에서 Fuzzing harness 생성 시 사용한 예시를 인용하는 방식으로 제공해주었습니다.

```
//Problem & Solution Example

If an example provided for the same library includes a unique header file, then it must be included in the solution as well.

Problem:

```

BGD_DECLARE(void) gdImageString (gdImagePtr im, gdFontPtr f, int x, int y, unsigned char *s, int color)

```

Solution:

```

#include <fuzzer/FuzzedDataProvider.h>

#include <cstddef>

#include <cstdint>

#include <cstdlib>

#include <string>

#include "gd.h"

#include "gdfontg.h"

#include "gdfontl.h"

#include "gdfontmb.h"

#include "gdfonts.h"

#include "gdfontt.h"

extern "C" int LLVMFuzzerTestOneInput(const uint8_t* data, size_t size) {

FuzzedDataProvider stream(data, size);

const uint8_t slate_width = stream.ConsumeIntegral<uint8_t>();

const uint8_t slate_height = stream.ConsumeIntegral<uint8_t>();

gdImagePtr slate_image = gdImageCreateTrueColor(slate_width, slate_height);

if (slate_image == nullptr) {

return 0;

}

const int x_position = stream.ConsumeIntegral<int>();

const int y_position = stream.ConsumeIntegral<int>();

const int text_color = stream.ConsumeIntegral<int>();

const gdFontPtr font_ptr = stream.PickValueInArray(

{gdFontGetGiant(), gdFontGetLarge(), gdFontGetMediumBold(),

gdFontGetSmall(), gdFontGetTiny()});

const std::string text = stream.ConsumeRemainingBytesAsString();

gdImageString(slate_image, font_ptr, x_position, y_position,

reinterpret_cast<uint8_t*>(const_cast<char*>(text.c_str())),

text_color);

gdImageDestroy(slate_image);

return 0;

}

```

Problem:

```

MPG123_EXPORT int mpg123_decode(mpg123_handle *mh, const unsigned char *inmemory, size_t inmemsize, unsigned char *outmemory, size_t outmemsize, size_t *done )

```

Solution:

```

#include <fuzzer/FuzzedDataProvider.h>

#include <cstddef>

#include <cstdint>

#include <cstdio>

#include <cstdlib>

#include <vector>

#include "mpg123.h"

extern "C" int LLVMFuzzerTestOneInput(const uint8_t* data, size_t size) {

static bool initialized = false;

if (!initialized) {

mpg123_init();

initialized = true;

}

int ret;

mpg123_handle* handle = mpg123_new(nullptr, &ret);

if (handle == nullptr) {

return 0;

}

ret = mpg123_param(handle, MPG123_ADD_FLAGS, MPG123_QUIET, 0.);

if(ret == MPG123_OK)

ret = mpg123_open_feed(handle);

if (ret != MPG123_OK) {

mpg123_delete(handle);

return 0;

}

std::vector<uint8_t> output_buffer(mpg123_outblock(handle));

size_t output_written = 0;

// Initially, start by feeding the decoder more data.

int decode_ret = MPG123_NEED_MORE;

FuzzedDataProvider provider(data, size);

while ((decode_ret != MPG123_ERR)) {

if (decode_ret == MPG123_NEED_MORE) {

if (provider.remaining_bytes() == 0

|| mpg123_tellframe(handle) > 10000

|| mpg123_tell_stream(handle) > 1<<20) {

break;

}

const size_t next_size = provider.ConsumeIntegralInRange<size_t>(

0,

provider.remaining_bytes());

auto next_input = provider.ConsumeBytes<unsigned char>(next_size);

decode_ret = mpg123_decode(handle, next_input.data(), next_input.size(),

output_buffer.data(), output_buffer.size(),

&output_written);

} else if (decode_ret != MPG123_ERR && decode_ret != MPG123_NEED_MORE) {

decode_ret = mpg123_decode(handle, nullptr, 0, output_buffer.data(),

output_buffer.size(), &output_written);

} else {

// Unhandled mpg123_decode return value.

abort();

}

}

mpg123_delete(handle);

return 0;

}
```

### 5. **Define Target Method**

- 최종적으로 타겟으로 하고 싶은 메소드에 대한 데이터를 제공해주는 부분입니다.
- 타겟 메소드의 선언(함수명, 파라미터명, 타입 등)을 [target Method Data]에 입력합니다.
- Solution: 부분을 통해 LLM이 해당 타겟 메소드에 대한 Fuzzing harness를 제공해줍니다.

```
//define target method

```

You MUST call '[target Method Data]' in your solution!

Problem:

```

[target Method Data]

Solution:
```

## **Fix Prompt**

Fix Prompt는 LLM에게 Generate Prompt를 통해 제공받은 Fuzzing harness가 잘 빌드되지 않을 때, 해당 빌드 오류를 수정하기 위한 프롬프트입니다.

Fix Prompt는 아래와 같은 형식을 가지고 있습니다.

```
Given the following C program and its build error message, fix the code without affecting its functionality. First explain the reason, then output the whole fixed code.

If a function is missing, fix it by including the related libraries.

And show the fixed code as code with line breaks

Code:

[Error Harness]

```

Build error message:

[Build Error Message]

Fixed code:
```

Fix Prompt의 각 부분에 대한 설명을 해드리겠습니다.

### 1. **상황 설정 및 주의사항 제시**

- LLM에게 역할을 부여해주고, 현재 무슨 상황인지 설명해줍니다.
- Fuzzing harness에 대한 빌드 오류를 해결하라는 명령을 내려줍니다.

```
Given the following C program and its build error message, fix the code without affecting its functionality. First explain the reason, then output the whole fixed code.

If a function is missing, fix it by including the related libraries.

And show the fixed code as code with line breaks
```

### 

### 2. **Error Harness 및 Error Message 제시**

- 현재 오류가 발생한 Fuzzing harness 코드 전체를 [Error Message]에 제공해줍니다.
- 발생한 빌드 오류 메시지를 [Build Error Message]에 제공해줍니다.
- Fixed code: 부분을 통해 LLM이 빌드 오류를 수정한 새로운 Fuzzing harness를 제공해줄 것 입니다.

```
Code:

[Error Harness]

```

Build error message:

[Build Error Message]

Fixed code:
```

다음으로는 Fuzzing 과정, 그리고 자동화 부분에 사용한 기술에 대해 설명해 드리겠습니다.

## **웹 프로젝트 구현**

편리한 사용을 위해 Spring MVC 스택을 기반으로 웹 프로젝트를 구축했습니다. 사용자가 프로그램명, 버전, 타겟 함수의 정보를 입력 시 적절한 Fuzzing harness를 자동 생성하여 제공합니다.

자동 생성을 위해 요청할 Prompt(Generate Prompt, Fix Prompt)를 txt파일로 작성후, 해당 파일을 Read하여 GPT4.0 API에 Webflux의 WebClient기반 비동기 요청을 보내고, 리턴된 정보를 Reactive하게 수신하는 로직을 통해 NetWork I/O Cost를 완화했습니다.

다음으로는 해당 기술 구현에 있어서 어려웠던 점에 대해 설명해 드리겠습니다.

**사용 기술 스택: GCP, Linux, Java, Spring, GPT-API, AFL++**

GCP를 통해 가상화된 Linux 환경을 사용하여 Fuzzing을 수행했습니다. 동시에 여러 Fuzzing harness를 테스트하기 위해 Tmux를 통해 다수의 세션에서 Fuzzing을 수행하였고, 다수의 세션에서 돌아가는 Fuzzing이 원활하게 수행되도록 CPU 코어 개수를 16개로 설정했습니다.

# Trouble shooting

LLM(GPT)이 Compile되는 Harness 코드를 생성하도록 프롬프트를 구성하는 것에 어려움이 있었습니다. GPT의 경우 블랙박스 영역이 넓기에 오픈소스로 남겨져 있는 코드 중, 타겟 프로그램과 관련없는 코드를 인식하여 사용할 수 없는 메소드를 사용하거나, 타겟 프로그램의 헤더파일을 올바르게 가져오지 못하는 문제가 발생했습니다. 이를 해결하고자 헤더 파일 Include에 대한 별도의 가이드 라인을 작성하여 관련 문제를 최소화할 수 있었습니다.

또한 버전의 진화로 인해 deprecated된 메소드 역시 사용했기에 관련 가이드 라인, 그리고 Warning문을 작성하는 것으로 관련 문제를 최소화할 수 있었습니다.

# **성과**

- 프로젝트를 통해 프로그램명, 버전, 타겟 함수만 입력하면 제너럴하게 Compile 가능한 Harness Code를 자동으로 생성할 수 있었습니다.
- 생성된 Harness를 통한 Fuzzing 내에서 코드 커버리지 지표와 관련된 Map density가 향상되었습니다.

## **활용 방안**

1. **소프트웨어 취약점 발견의 효율성 향상**

Fuzzing은 취약점 발견에 효과적이지만, Fuzzing Harness 작성은 전문 지식이 필요하여 진입 장벽이 높습니다. 프로젝트 결과를 통해 누구나 손쉽게 Fuzzing을 수행할 수 있게 되면, 소프트웨어 취약점 발견의 효율성이 크게 향상될 것으로 기대됩니다.

1. **Fuzzing Harness 작성의 자동화**

Fuzzing Harness는 프로그램의 변화에 따라 수정이 필요한 경우가 많습니다. 자동으로 생성된 Harness를 통해 개발자는 작업의 효율성을 높일 수 있습니다.

1. **코드 커버리지 향상을 통한 안정성 확보**

Fuzzing Harness의 자동 생성은 코드 커버리지를 향상시키는 데 기여할 수 있습니다. 안정성 확보를 위해 자동 생성된 Harness를 활용하여 새로운 테스트 케이스를 수행하고 코드 커버리지를 높일 수 있습니다.

## **추후 개선 방안**

1. **Bug Detection을 고려한 Prompt 수정**

생성된 Harness를 통해 명시된 Bug를 감지할 수 있는 방안을 고려하여 Prompt를 수정할 필요가 있습니다. 이를 통해 자동으로 발견된 Bug를 개발자에게 알려주는 기능을 추가할 수 있습니다.

1. **Interactive Learning 도입**:

LLM이 학습하지 않거나 학습량이 적은 타겟 프로그램에 대한 대응을 강화해야 합니다. LLM의 학습 정도에 종속되는 문제를 해결하기 위해, 학습이 미비한 경우에도 올바른 Harness를 생성할 수 있는 방안을 연구하고 구현할 필요가 있습니다. 이에 따라 사용자와 상호작용하여 더 정확한 학습을 가능케 하는 Interactive Learning을 도입하고, 사용자가 생성된 Harness에 대한 피드백을 주고받으면서 모델이 사용자의 요구에 더욱 적합하게 학습하도록 유도한다면 이러한 문제를 개선할 수 있습니다.

1. **Generate Prompt 및 Fix Prompt 프로세스 자동화**

Generate Prompt 및 Fix Prompt의 프로세스를 완전 자동화하여 백그라운드에서 gernerate와 feedback을 반복하여 다양한 Harness를 생성할 수 있도록 시스템을 구축한다면 좋은 성과를 얻을 수 있을 것 같습니다. Generate Prompt로 Harness를 다수 생성하고, 각 harness가 Build 에러를 도출하거나, 적절하지 못한 Harness일시 자동으로 문제점을 감지하여 해당 문제에 대한 Fix Prompt를 통해 Valid한 Harness로 수정시켜 나간다면, 다양한 Harness들을 백그라운드에서 생성하여 유효한 결과를 도출할 확률을 높일 수 있습니다.

## **결론 및 기대효과**

프로젝트를 통해 Fuzzing Harness의 자동 생성 및 수정 과정을 완전 자동화함으로써, 다양한 소프트웨어에 대한 Fuzzing을 누구나 손쉽게 수행할 수 있는 기술적 기반을 마련하였습니다. 이로써 소프트웨어 취약점 발견과 안정성 강화에 기여하며, 개발자들의 업무 효율성을 향상시킬 것으로 기대됩니다. 향후 연구 및 발전을 통해 미리 학습되지 않은 타겟 프로그램에 대한 대응력을 향상시키고, 더욱 효과적인 Bug Detection 기능을 추가하는 방향으로 발전시켜 나가는 것이 필요합니다.